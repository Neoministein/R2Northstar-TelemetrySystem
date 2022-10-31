package com.neo.r2.ts.impl.rest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.MatchFacade;
import com.neo.r2.ts.impl.match.MatchService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.rest.dto.HitsDto;
import com.neo.r2.ts.impl.rest.dto.MatchDto;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.parser.ValidateJsonSchema;
import com.neo.util.framework.rest.api.response.ResponseGenerator;
import com.neo.util.framework.rest.api.security.Secured;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path(MatchResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchResource {

    public static final String RESOURCE_LOCATION = "api/v1/match";

    public static final String P_NEW = "/new";
    public static final String P_END = "/end";

    public static final String P_PLAYING = "/playing";
    public static final String P_STOPPED = "/stopped";

    public static final String P_HEATMAP = "/heatmap";

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected MatchService matchService;

    @Inject
    protected MatchFacade matchFacade;

    @Inject
    protected ResponseGenerator responseGenerator;

    @POST
    @Secured
    @Path(P_NEW)
    @ValidateJsonSchema("NewMatch.json")
    public Response newGame(JsonNode jsonNode) {
        return responseGenerator.success(JsonUtil.fromPojo(matchFacade.createNewMatch(
                        jsonNode.get("map").asText(),
                        jsonNode.get("ns_server_name").asText(),
                        jsonNode.get("gamemode").asText()), Views.Public.class));
    }

    @PUT
    @Secured
    @Path(P_END + "/{id}")
    public Response endGame(@PathParam("id") String id) {
        return responseGenerator.success(JsonUtil.fromPojo(
                matchFacade.endMatch(id), Views.Public.class));
    }

    @GET
    @Path(P_PLAYING)
    @Transactional
    public Response playing() {
        List<MatchDto> matchDtoList = matchService.getArePlaying().stream()
                .map(match -> new MatchDto(match, matchStateService.getNumberOfPlayerInMatch(match.getId()))).toList();

        return responseGenerator.success(JsonUtil.fromPojo(new HitsDto(matchDtoList), Views.Public.class));
    }

    @GET
    @Path("/{id}")
    public Response match(@PathParam("id") String id) {
        return responseGenerator.success(JsonUtil.fromPojo(
                matchFacade.getMatch(id), Views.Public.class));
    }

    @GET
    @Path(P_STOPPED)
    @Transactional
    public Response stopped() {
        return responseGenerator.success(JsonUtil.fromPojo(matchService.getStoppedPlaying(), Views.Public.class));
    }

    @GET
    @Path("/{id}" + P_HEATMAP)
    @Transactional
    public Response getHeatmap(@PathParam("id") String id) {
        return responseGenerator.success(
          JsonUtil.fromPojo(matchFacade.getHeatmapOfMatch(id), Views.Public.class)
        );
    }
}
