package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.framework.rest.api.response.ResponseGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomRestRestResponse {

    protected final ObjectNode unsupportedMap;
    protected final ObjectNode matchAlreadyEnded;

    protected final ObjectNode service;
    protected final ObjectNode forbidden;

    @Inject
    public CustomRestRestResponse(ResponseGenerator responseGenerator) {
        unsupportedMap = responseGenerator.errorObject("tmly/000","Unknown or unsupported map");
        matchAlreadyEnded = responseGenerator.errorObject("tmly/001","Match has already ended");
        service = responseGenerator.errorObject("svc/000", "Internal service not available");
        forbidden = responseGenerator.errorObject("auth/000", "Unauthorized");
    }

    public ObjectNode getUnsupportedMap() {
        return unsupportedMap;
    }

    public ObjectNode getMatchAlreadyEnded() {
        return matchAlreadyEnded;
    }

    public ObjectNode getService() {
        return service;
    }

    public ObjectNode getForbidden() {
        return forbidden;
    }
}
