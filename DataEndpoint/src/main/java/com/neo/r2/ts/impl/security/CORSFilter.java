package com.neo.r2.ts.impl.security;

import com.neo.util.framework.rest.api.response.ResponseGenerator;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
            final ContainerResponseContext containerRequest) {
        containerRequest.getHeaders().add("Access-Control-Expose-Headers", ResponseGenerator.VALID_BACKEND_ERROR);
        containerRequest.getHeaders().add("Access-Control-Allow-Origin", "*");
        containerRequest.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        containerRequest.getHeaders().add("Access-Control-Allow-Credentials", "true");
        containerRequest.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        containerRequest.getHeaders().add("Access-Control-Max-Age", "1209600");
    }

}