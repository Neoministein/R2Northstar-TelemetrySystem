package com.neo.tf2.ms.impl.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
            final ContainerResponseContext containerRequest) {
        containerRequest.getHeaders().add("Access-Control-Allow-Origin", "*");
        containerRequest.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        containerRequest.getHeaders().add("Access-Control-Allow-Credentials", "true");
        containerRequest.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        containerRequest.getHeaders().add("Access-Control-Max-Age", "1209600");
    }

}