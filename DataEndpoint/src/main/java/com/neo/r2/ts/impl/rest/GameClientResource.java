package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Path(GameClientResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class GameClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientResource.class);

    public static final String RESOURCE_LOCATION = "api/client";

    @GET
    @Path("compatibility/{version}")
    public JsonNode isCompatible(@PathParam("version") String version) {
        LOGGER.info("Checking for client version compatibility [{}] compatible [true]", version);
        ObjectNode response =  JsonUtil.emptyObjectNode();
        response.put("compatible",true);
        return response;
    }
}
