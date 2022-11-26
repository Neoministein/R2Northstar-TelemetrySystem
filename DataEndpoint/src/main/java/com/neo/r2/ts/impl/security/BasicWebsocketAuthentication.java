package com.neo.r2.ts.impl.security;

import com.neo.r2.ts.impl.rest.AuthorizationEndpoint;
import com.neo.util.common.impl.exception.CommonRuntimeException;
import com.neo.util.common.impl.retry.RetryHttpExecutor;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.ws.rs.core.HttpHeaders;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;

public class BasicWebsocketAuthentication extends ServerEndpointConfig.Configurator {

    protected static final RetryHttpExecutor RETRY_HTTP_EXECUTOR = new RetryHttpExecutor();

    protected static final URI ENDPOINT_URI = URI.create("http://localhost:8090/" + AuthorizationEndpoint.RESOURCE_LOCATION);

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            List<String> authorization = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (!authorization.isEmpty()) {
                String authenticationHeader = authorization.get(0);
                if (isTokenValid(authenticationHeader)) {
                    return;
                }
            }
        }
        TyrusUpgradeResponse tyrusUpgradeResponse = (TyrusUpgradeResponse) response;
        tyrusUpgradeResponse.setStatus(403);
    }

    protected boolean isTokenValid(String authenticationHeader) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(ENDPOINT_URI).POST(HttpRequest.BodyPublishers.noBody())
                    .header(HttpHeaders.AUTHORIZATION, authenticationHeader).build();
            RETRY_HTTP_EXECUTOR.execute(HttpClient.newHttpClient(), request,5);
            return true;
        } catch (CommonRuntimeException ex) {
            return false;
        }
    }
}