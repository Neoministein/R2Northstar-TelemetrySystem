package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.response.ResponseGenerator;
import com.neo.util.framework.rest.api.security.Secured;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path(GameClientResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class GameClientResource {

    public static final String RESOURCE_LOCATION = "api/client";

    @Inject
    ResponseGenerator responseGenerator;

    @GET
    public Response get() {
        ObjectNode node =  JsonUtil.emptyObjectNode();
        node.put("newestVersion","1.0");
        node.put("requiredVersion","1.0");
        return responseGenerator.success(node);
    }
}
