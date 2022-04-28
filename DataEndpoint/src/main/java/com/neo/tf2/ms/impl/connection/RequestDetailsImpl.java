package com.neo.tf2.ms.impl.connection;

import com.neo.javax.api.connection.RequestDetails;
import io.helidon.security.SecurityContext;
import io.helidon.webserver.ServerRequest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Context;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class RequestDetailsImpl implements RequestDetails {

    @Context
    ServerRequest serverRequest;

    @Context
    SecurityContext securityContext;


    public String getRemoteAddress() {
        return serverRequest.remoteAddress();
    }

    public String getRequestId() {
        return securityContext.id();
    }

    public Optional<UUID> getUUId() {
        return securityContext.user().map(subject -> UUID.fromString(subject.principal().id()));
    }

    public boolean isInRole(String role) {
        return securityContext.isUserInRole(role);
    }
}
