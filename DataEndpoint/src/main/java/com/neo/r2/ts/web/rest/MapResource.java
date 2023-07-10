package com.neo.r2.ts.web.rest;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.map.heatmap.HeatmapFactory;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScale;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.repository.HeatmapRepository;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.framework.rest.api.parser.OutboundJsonView;
import com.neo.util.framework.rest.api.security.Secured;
import com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path(MapResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MapResource {

    public static final String RESOURCE_LOCATION = "api/v1/map/";

    @Inject
    protected MapService mapService;

    @Inject
    protected HeatmapFactory heatmapFactory;

    @Inject
    protected HeatmapRepository heatmapRepository;

    @GET
    public HitsDto getAllMaps() {
        return new HitsDto(mapService.fetchAll());
    }

    @GET
    @Path("{map}")
    public GameMap getMapByName(@PathParam("map") String map) {
        return mapService.requestMap(map);
    }

    @GET
    @Path("{map}/scale")
    public MapScale getMapScaleByName(@PathParam("map") String map) {
        return mapService.requestMap(map).scale();
    }

    @GET
    @Path("{map}/heatmap")
    @OutboundJsonView(Views.Public.class)
    public Heatmap getHeatmapData(@PathParam("map") String map) {
        return heatmapRepository.fetchFullMapAggregation(getMapByName(map).name()).orElseThrow(() ->
                new NoContentFoundException(CustomConstants.EX_NO_HEATMAP_FOR_MATCH, map));
    }

    @POST
    @Path("{map}/heatmap")
    @Secured
    @RolesAllowed(AbstractEntityRestEndpoint.PERM_INTERNAL)
    public Heatmap createHeatmapData(@PathParam("map") String map) {
        Heatmap heatmap = heatmapFactory.createForMap(getMapByName(map));
        heatmapRepository.create(heatmap);
        return heatmap;
    }
}
