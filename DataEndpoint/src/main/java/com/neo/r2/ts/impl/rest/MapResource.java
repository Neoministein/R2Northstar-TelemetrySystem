package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.InternalLogicException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.r2.ts.impl.map.heatmap.HeatmapGeneratorImpl;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.security.Secured;
import com.neo.util.framework.api.connection.RequestDetails;
import com.neo.util.framework.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persitence.entity.EntityQuery;
import com.neo.util.framework.api.persitence.entity.EntityRepository;
import com.neo.util.framework.persistence.impl.AuditableDataBaseEntity;
import com.neo.util.framework.rest.api.RestAction;
import com.neo.util.framework.rest.impl.DefaultResponse;
import com.neo.util.framework.rest.impl.RestActionProcessor;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@RequestScoped
@Path(MapResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MapResource {

    public static final String RESOURCE_LOCATION = "api/v1/map/";

    @Inject
    RequestDetails requestDetails;

    @Inject
    EntityRepository entityRepository;

    @Inject
    MapScalingService mapScalingService;

    @Inject
    HeatmapGeneratorImpl heatmapGenerator;

    @Inject
    RestActionProcessor actionProcessor;

    @GET
    public Response get() {
        RestAction restAction = () -> {
            try {
                List<GameMap> gameMaps = mapScalingService.getMaps();
                ObjectNode resultData = JsonUtil.emptyObjectNode();
                resultData.set("hits",JsonUtil.fromPojo(gameMaps));
                resultData.put("hitCount", gameMaps.size());

                return DefaultResponse.success(requestDetails.getRequestContext(),resultData);
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return actionProcessor.process(restAction);
    }

    @GET
    @Path("{map}")
    public Response get(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.fromPojo(mapScalingService.getMap(map)));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return actionProcessor.process(restAction);
    }

    @GET
    @Path("scaling/{map}")
    public Response getScaling(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.fromPojo(mapScalingService.getMapScale(map)));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(400, CustomRestRestResponse.E_UNSUPPORTED_MAP, requestDetails.getRequestContext());
            }
        };
        return actionProcessor.process(restAction);
    }

    @GET
    @Path("heatmap/{map}")
    public Response getHeatmapData(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                EntityQuery<Heatmap> heatmapEntityQuery = new EntityQuery<>(
                        Heatmap.class,
                        0,
                        1,
                        List.of(new ExplicitSearchCriteria(Heatmap.C_MAP, map),
                                new ExplicitSearchCriteria(Heatmap.C_TYPE, HeatmapType.FULL_MAP_AGGREGATION)),
                        Map.of(AuditableDataBaseEntity.C_UPDATED_ON, false));

                String result = JsonUtil.toJson(entityRepository.find(heatmapEntityQuery), Views.Public.class);
                return DefaultResponse.success(this.requestDetails.getRequestContext(), JsonUtil.fromJson(result));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(503, CustomRestRestResponse.E_SERVICE,requestDetails.getRequestContext());
            }
        };
        return actionProcessor.process(restAction);
    }

    @POST
    @Secured
    @Path("heatmap/{map}")
    public Response createHeatmapData(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                String result = JsonUtil.toJson(heatmapGenerator.calculateMap(map), Views.Public.class);
                return DefaultResponse.success(this.requestDetails.getRequestContext(), JsonUtil.fromJson(result));
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(503, CustomRestRestResponse.E_SERVICE,requestDetails.getRequestContext());
            }
        };
        return actionProcessor.process(restAction);
    }
}
