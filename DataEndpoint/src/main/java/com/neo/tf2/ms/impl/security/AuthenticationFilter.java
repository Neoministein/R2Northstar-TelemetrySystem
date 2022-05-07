package com.neo.tf2.ms.impl.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;
import com.neo.util.javax.impl.rest.DefaultResponse;
import com.neo.util.javax.impl.rest.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    protected static final ObjectNode E_UNAUTHORIZED = DefaultResponse.errorObject("auth/000", "Unauthorized");

    protected static final String AUTHENTICATION_SCHEME = "Bearer";

    @Inject
    RequestContext requestContext;

    @Inject
    AuthenticationService authenticationService;

    @Override
    public void filter(ContainerRequestContext containerRequest) {
        String authorizationHeader = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);


        if (!validateAuthorizationHeader(authorizationHeader)) {
            abortWithUnauthorized(containerRequest);
            return;
        }

        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        if (!validateToken(token)) {
            abortWithUnauthorized(containerRequest);
        }
    }

    private boolean validateAuthorizationHeader(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext containerRequest) {
        com.neo.util.javax.impl.rest.RequestContext requestContext = new com.neo.util.javax.impl.rest.RequestContext(HttpMethod.valueOf(containerRequest.getMethod()), getClassUri(containerRequest.getUriInfo()),"");
        containerRequest.abortWith(DefaultResponse.error(401,E_UNAUTHORIZED, requestContext));
    }

    private boolean validateToken(String token) {
        Optional<UserToken> optionalToken = authenticationService.retrieveToken(token);
        if (optionalToken.isPresent()) {
            UserToken userToken = optionalToken.get();
            requestContext.setUser(userToken.getOwner());
            requestContext.setRoles(userToken.getRoles());
            return true;
        }
        return false;
    }

    protected String getClassUri(UriInfo uriInfo) {
        return uriInfo.getRequestUri().toString().substring(uriInfo.getBaseUri().toString().length());
    }
}