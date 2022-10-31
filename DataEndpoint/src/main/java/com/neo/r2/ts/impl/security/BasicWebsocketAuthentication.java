package com.neo.r2.ts.impl.security;

import com.neo.r2.ts.impl.rest.AuthorizationEndpoint;
import com.neo.util.common.impl.exception.CommonRuntimeException;
import com.neo.util.common.impl.retry.RetryHttpExecutor;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;

import java.util.List;

public class BasicWebsocketAuthentication extends ServerEndpointConfig.Configurator {

    protected static final RetryHttpExecutor RETRY_HTTP_EXECUTOR = new RetryHttpExecutor();

    protected static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

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
            HttpPost postRequest = new HttpPost("http://localhost:8090/" + AuthorizationEndpoint.RESOURCE_LOCATION);
            postRequest.addHeader(HttpHeaders.AUTHORIZATION, authenticationHeader);
            RETRY_HTTP_EXECUTOR.execute(HTTP_CLIENT, postRequest,5);
            return true;
        } catch (CommonRuntimeException ex) {
            return false;
        }
    }
}