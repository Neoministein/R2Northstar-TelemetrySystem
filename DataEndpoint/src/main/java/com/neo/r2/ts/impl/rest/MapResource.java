package com.neo.r2.ts.impl.rest;

import com.neo.r2.ts.impl.map.MapFacade;
import com.neo.r2.ts.impl.rest.dto.HitsDto;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.response.ResponseGenerator;
import com.neo.util.framework.rest.api.security.Secured;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path(MapResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MapResource {

    public static final String RESOURCE_LOCATION = "api/v1/map/";

    @Inject
    protected ResponseGenerator responseGenerator;

    @Inject
    protected MapFacade mapFacade;

    @GET
    public Response get() {
        return responseGenerator.success(JsonUtil.fromPojo((new HitsDto(mapFacade.getMaps()))));
    }

    @GET
    @Path("{map}")
    public Response get(@PathParam("map") String map) {
        return responseGenerator.success(JsonUtil.fromPojo((mapFacade.getMap(map))));
    }

    @GET
    @Path("{map}/scale")
    public Response getScaling(@PathParam("map") String map) {
        return responseGenerator.success(JsonUtil.fromPojo(mapFacade.getMapScale(map)));
    }

    @GET
    @Path("{map}/heatmap")
    public Response getHeatmapData(@PathParam("map") String map) {
        return responseGenerator.success(JsonUtil.fromPojo(mapFacade.getHeatmapOfMap(map), Views.Public.class));
    }

    @POST
    @Secured
    @Path("{map}/heatmap")
    public Response createHeatmapData(@PathParam("map") String map) {
        return responseGenerator.success(JsonUtil.fromPojo(mapFacade.generateHeatmapForMap(map)));
    }
}
