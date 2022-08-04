package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.map.heatmap.HeatmapGeneratorImpl;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScale;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.InternalLogicException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import com.neo.util.framework.persistence.impl.AuditableDataBaseEntity;
import com.neo.util.framework.rest.api.response.ResponseGenerator;
import com.neo.util.framework.rest.api.security.Secured;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestScoped
@Path(MapResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MapResource {

    public static final String RESOURCE_LOCATION = "api/v1/map/";

    @Inject
    protected EntityRepository entityRepository;

    @Inject
    protected ResponseGenerator responseGenerator;

    @Inject
    protected CustomRestRestResponse customRestRestResponse;

    @Inject
    protected MapScalingService mapScalingService;

    @Inject
    protected HeatmapGeneratorImpl heatmapGenerator;

    @GET
    public Response get() {
        try {
            List<GameMap> gameMaps = mapScalingService.getMaps();
            ObjectNode resultData = JsonUtil.emptyObjectNode();
            resultData.set("hits",JsonUtil.fromPojo(gameMaps));
            resultData.put("hitCount", gameMaps.size());

            return responseGenerator.success(resultData);
        } catch (InternalLogicException ex) {
            return responseGenerator.error(400, customRestRestResponse.getUnsupportedMap());
        }
    }

    @GET
    @Path("{map}")
    public Response get(@PathParam("map") String map) {
        Optional<GameMap> optionalGameMap = mapScalingService.getMap(map);
        if (optionalGameMap.isPresent()) {
            return responseGenerator.success(JsonUtil.fromPojo(optionalGameMap.get()));
        }
        return responseGenerator.error(400, customRestRestResponse.getUnsupportedMap());
    }

    @GET
    @Path("{map}/scale")
    public Response getScaling(@PathParam("map") String map) {
        Optional<MapScale> optionalGameMap = mapScalingService.getMapScale(map);
        if (optionalGameMap.isPresent()) {
            return responseGenerator.success(JsonUtil.fromPojo(optionalGameMap.get()));
        }
        return responseGenerator.error(400, customRestRestResponse.getUnsupportedMap());
    }

    @GET
    @Path("{map}/heatmap")
    public Response getHeatmapData(@PathParam("map") String map) {
        try {
            EntityQuery<Heatmap> heatmapEntityQuery = new EntityQuery<>(
                    Heatmap.class,
                    0,
                    1,
                    List.of(new ExplicitSearchCriteria(Heatmap.C_MAP, map),
                            new ExplicitSearchCriteria(Heatmap.C_TYPE, HeatmapType.FULL_MAP_AGGREGATION)),
                    Map.of(AuditableDataBaseEntity.C_UPDATED_ON, false));

            String result = JsonUtil.toJson(entityRepository.find(heatmapEntityQuery), Views.Public.class);
            return responseGenerator.success(JsonUtil.fromJson(result));
        } catch (InternalLogicException ex) {
            return responseGenerator.error(503, customRestRestResponse.getService());
        }
    }

    @POST
    @Secured
    @Path("{map}/heatmap")
    public Response createHeatmapData(@PathParam("map") String map) {
        try {
            String result = JsonUtil.toJson(heatmapGenerator.calculateMap(map), Views.Public.class);
            return responseGenerator.success(JsonUtil.fromJson(result));
        } catch (InternalLogicException ex) {
            return responseGenerator.error(503, customRestRestResponse.getService());
        }
    }
}
