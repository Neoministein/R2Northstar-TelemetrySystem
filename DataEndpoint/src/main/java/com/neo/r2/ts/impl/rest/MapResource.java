package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.impl.exception.InternalLogicException;
import com.neo.common.impl.json.JsonUtil;
import com.neo.r2.ts.impl.map.heatmap.HeatmapImpl;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.AbstractRestEndpoint;
import com.neo.util.javax.impl.rest.DefaultResponse;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path(MapResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MapResource extends AbstractRestEndpoint {

    public static final String RESOURCE_LOCATION = "api/v1/map/";

    @Inject
    MapScalingService mapScalingService;

    @Inject
    HeatmapImpl heatmap;

    @GET
    @Path("")
    public Response get() {
        RestAction restAction = () -> {
            try {
                List<GameMap> gameMaps = mapScalingService.getMaps();
                ObjectNode resultData = JsonUtil.emptyObjectNode();
                resultData.set("hits",JsonUtil.toArrayNode(gameMaps));
                resultData.put("hitCount", gameMaps.size());

                return DefaultResponse.success(requestDetails.getRequestContext(),resultData);
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return super.restCall(restAction);
    }

    @GET
    @Path("{map}")
    public Response get(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.toObjectNode(mapScalingService.getMap(map)));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return super.restCall(restAction);
    }

    @GET
    @Path("scaling/{map}")
    public Response getScaling(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.toObjectNode(mapScalingService.getMapScale(map)));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return super.restCall(restAction);
    }

    @GET
    @Path("heatmap/{map}")
    public Response heatmapData(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                JsonNode result = heatmap.calculate(map);
                return DefaultResponse.success(requestDetails.getRequestContext(), result);
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(503, CustomRestRestResponse.E_SERVICE,requestDetails.getRequestContext());
            }
        };
        return super.restCall(restAction);
    }
}
