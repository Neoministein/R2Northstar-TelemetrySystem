package com.neo.tf2.ms.impl.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.javax.api.connection.RequestDetails;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;
import com.neo.util.javax.impl.rest.DefaultResponse;
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
import java.util.ArrayList;
import java.util.Optional;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    protected static final ObjectNode E_UNAUTHORIZED = DefaultResponse.errorObject("auth/000", "Unauthorized");

    protected static final String AUTHENTICATION_SCHEME = "Bearer";

    @Inject
    RequestUser requestContext;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    RequestDetails requestDetails;

    @Override
    public void filter(ContainerRequestContext containerRequest) {
        LOGGER.debug("Authentication attempt");
        String authorizationHeader = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);


        if (!validateAuthorizationHeader(authorizationHeader)) {
            LOGGER.debug("Invalid authorization header");
            abortWithUnauthorized(containerRequest);
            return;
        }

        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            if (validateToken(token)) {
                return;
            }
        } catch (Exception ex) {
            LOGGER.error("An error occurred when trying to authenticate the user", ex);
        }
        abortWithUnauthorized(containerRequest);
    }

    private boolean validateAuthorizationHeader(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext containerRequest) {
        containerRequest.abortWith(DefaultResponse.error(401,E_UNAUTHORIZED, requestDetails.getRequestContext()));
    }

    private boolean validateToken(String token) {
        Optional<UserToken> optionalToken = authenticationService.retrieveToken(token);
        if (optionalToken.isPresent()) {
            LOGGER.debug("Authentication success");
            UserToken userToken = optionalToken.get();
            requestContext.setUser(userToken.getOwner());
            requestContext.setRoles(new ArrayList<>(userToken.getRoles()));
            return true;
        }
        LOGGER.debug("Authentication failure");
        return false;
    }
}