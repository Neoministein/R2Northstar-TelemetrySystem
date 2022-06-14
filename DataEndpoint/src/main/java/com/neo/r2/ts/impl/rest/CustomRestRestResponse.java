package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.framework.rest.impl.DefaultResponse;

public final class CustomRestRestResponse {

    public static final ObjectNode E_UNSUPPORTED_MAP =  DefaultResponse.errorObject("tmly/000","Unknown or unsupported map");
    public static final ObjectNode E_MATCH_ALREADY_ENDED = DefaultResponse.errorObject("tmly/000","Match has already ended");

    public static final ObjectNode E_SERVICE = DefaultResponse.errorObject("svc/000", "Internal service not available");

    private CustomRestRestResponse(){}
}
