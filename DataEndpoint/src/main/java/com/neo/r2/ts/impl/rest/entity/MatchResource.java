package com.neo.r2.ts.impl.rest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.map.heatmap.HeatmapQueueService;
import com.neo.r2.ts.impl.map.heatmap.QueueableHeatmapInstruction;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.r2.ts.impl.rest.CustomRestRestResponse;
import com.neo.r2.ts.impl.match.GlobalMatchState;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.common.impl.exception.InternalLogicException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.queue.QueueMessage;
import com.neo.util.framework.rest.api.parser.ValidateJsonSchema;
import com.neo.util.framework.rest.api.security.Secured;
import com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Path(MatchResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchResource extends AbstractEntityRestEndpoint<Match> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchResource.class);

    protected static final EntityQuery<Match> Q_ARE_PLAYING = new EntityQuery<>(Match.class, 0,null,
            List.of(new ExplicitSearchCriteria(Match.C_IS_PLAYING,true)),
            Map.of(Match.C_START_DATE, false));

    protected static final EntityQuery<Match> Q_STOPPLED_PLAYING = new EntityQuery<>(Match.class, 0,null,
            List.of(new ExplicitSearchCriteria(Match.C_IS_PLAYING,false)),
            Map.of(Match.C_START_DATE, false));

    public static final String RESOURCE_LOCATION = "api/v1/match";

    public static final String P_NEW = "/new";
    public static final String P_END = "/end";

    public static final String P_PLAYING = "/playing";
    public static final String P_STOPPED = "/stopped";

    public static final String P_HEATMAP = "/heatmap";

    @Inject
    protected GlobalMatchState globalGameState;

    @Inject
    protected HeatmapQueueService heatmapQueueService;

    @Inject
    protected CustomRestRestResponse customRestRestResponse;

    @Inject
    protected MapScalingService mapScalingService;

    @POST
    @Secured
    @Path(P_NEW)
    @ValidateJsonSchema("schemas/NewMatch.json")
    public Response newGame(JsonNode jsonNode) {
        Match match = new Match();
        match.setMap(jsonNode.get("map").asText());
        match.setNsServerName(jsonNode.get("ns_server_name").asText());
        match.setGamemode(jsonNode.get("gamemode").asText());
        if (requestDetails.getUser().isPresent()) {
            match.setOwner(requestDetails.getUser().get().getName());
        }
        if (mapScalingService.getMap(match.getMap()).isEmpty()) {
            return responseGenerator.error(400, customRestRestResponse.getUnsupportedMap());
        }

        try {
            entityRepository.create(match);
        } catch (RollbackException ex) {
            throw new InternalLogicException(ex);
        }
        LOGGER.info("New match registered {}", match.getId());
        return parseEntityToResponse(match, Views.Public.class);
    }

    @PUT
    @Secured
    @Transactional
    @Path(P_END + "/{id}")
    public Response endGame(@PathParam("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return responseGenerator.error(404, errorNotFound);
        }
        try {
            Optional<Match> optMatch = entityRepository.find(UUID.fromString(id), Match.class);
            if (optMatch.isEmpty()) {
                return responseGenerator.error(404, errorNotFound);
            }
            Match match = optMatch.get();
            if (!match.getIsRunning()) {
                return responseGenerator.error(400, customRestRestResponse.getMatchAlreadyEnded());
            }
            match.setIsRunning(false);

            entityRepository.edit(match);
            globalGameState.removeGameState(match.getId());

            LOGGER.info("Match finished {}", match.getId());
            QueueableHeatmapInstruction queueableHeatmapInstruction = new QueueableHeatmapInstruction();
            queueableHeatmapInstruction.setMatchId(id);
            queueableHeatmapInstruction.setType(HeatmapType.PLAYER_POSITION);
            LOGGER.info("Add match {} to queue for heatmap generation", match.getId());
            heatmapQueueService.addToQueue(new QueueMessage(QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE, queueableHeatmapInstruction));
            return parseEntityToResponse(match, Views.Public.class);
        } catch (RollbackException ex) {
            throw new InternalLogicException(ex);
        } catch (IllegalArgumentException ex) {
            return responseGenerator.error(404, errorNotFound);
        }
    }

    @GET
    @Path(P_PLAYING)
    @Transactional
    public Response playing() {
        String result = JsonUtil.toJson(entityRepository.find(Q_ARE_PLAYING), Views.Public.class);
        return responseGenerator.success(JsonUtil.fromJson(result));
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response match(@PathParam("id") String id) {
        return super.getByPrimaryKey(id);
    }

    @GET
    @Path(P_STOPPED)
    @Transactional
    public Response stopped() {
        String result = JsonUtil.toJson(entityRepository.find(Q_STOPPLED_PLAYING), Views.Public.class);
        return responseGenerator.success(JsonUtil.fromJson(result));
    }

    @GET
    @Path(P_HEATMAP + "/{id}")
    @Transactional
    public Response getHeatmap(@PathParam("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return responseGenerator.error(404, errorNotFound);
        }
        try {
            Optional<Match> optMatch = entityRepository.find(UUID.fromString(id), Match.class);
            if (optMatch.isEmpty()) {
                return responseGenerator.error(404, errorNotFound);
            }

            return parseEntityToResponse(optMatch.get(), Views.Public.class);
        } catch (IllegalArgumentException ex) {
            return responseGenerator.error(404, errorNotFound);
        }
    }

    @Override
    protected Object convertToPrimaryKey(String s) {
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ex) {
            return new Object();
        }
    }

    @Override
    protected Class<Match> getEntityClass() {
        return Match.class;
    }
}
