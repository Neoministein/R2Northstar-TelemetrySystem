package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path(GameClientResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class GameClientResource {

    public static final String RESOURCE_LOCATION = "api/client";

    @GET
    public JsonNode get() {
        ObjectNode node =  JsonUtil.emptyObjectNode();
        node.put("newestVersion","1.0");
        node.put("requiredVersion","1.0");
        return node;
    }
}
