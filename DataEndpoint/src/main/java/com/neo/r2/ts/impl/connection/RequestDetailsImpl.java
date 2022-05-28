package com.neo.r2.ts.impl.connection;

import com.neo.javax.api.connection.RequestContext;
import com.neo.javax.api.connection.RequestDetails;
import com.neo.r2.ts.impl.security.RequestUser;
import io.helidon.security.SecurityContext;
import io.helidon.webserver.ServerRequest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class RequestDetailsImpl implements RequestDetails {

    protected RequestContext requestContext;

    protected Date receiveDate;

    @Inject
    RequestUser requestUser;

    @Context
    ServerRequest serverRequest;

    @Context
    SecurityContext securityContext;

    @Override
    public String getRemoteAddress() {
        return serverRequest.remoteAddress();
    }

    @Override
    public String getRequestId() {
        return securityContext.id();
    }

    @Override
    public Optional<UUID> getUUId() {
        return requestUser.getUser();
    }

    @Override
    public boolean isInRole(String role) {
        return requestUser.getRoles().contains(role);
    }

    @Override
    public boolean isInRoles(List<String> list) {
        return requestUser.getRoles().containsAll(list);
    }

    @Override
    public RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public void setRequestReceiveDate(Date date) {
        this.receiveDate = date;
    }

    @Override
    public Date getRequestReceiveDate() {
        return receiveDate;
    }

}
