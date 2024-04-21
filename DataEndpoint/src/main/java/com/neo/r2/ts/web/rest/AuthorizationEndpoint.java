package com.neo.r2.ts.web.rest;

import com.neo.util.framework.rest.api.security.SecuredResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path(AuthorizationEndpoint.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class AuthorizationEndpoint {

    public static final String RESOURCE_LOCATION = "api/v1/authorize";

    @POST
    @SecuredResource
    public Response authenticated() {
        return Response.ok().build();
    }
}
