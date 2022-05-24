package com.neo.r2.ts.impl.rest.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.api.json.Views;
import com.neo.common.impl.StringUtils;
import com.neo.common.impl.exception.InternalLogicException;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.entity.EntityQuery;
import com.neo.r2.ts.impl.security.Secured;
import com.neo.r2.ts.impl.persistence.GlobalGameState;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.DefaultResponse;

import com.neo.util.javax.impl.rest.entity.AbstractEntityRestEndpoint;
import com.networknt.schema.JsonSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.RollbackException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Path(MatchResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchResource extends AbstractEntityRestEndpoint<Match> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchResource.class);

    protected static final JsonSchema JSON_SCHEMA = JsonSchemaUtil.generateSchemaFromResource("schemas/NewMatch.json");

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

    @Inject
    GlobalGameState globalGameState;

    @POST
    @Secured
    @Path(P_NEW)
    public Response newGame(String x) {
        RestAction restAction = () -> {
            JsonNode json = JsonUtil.fromJson(x);
            JsonSchemaUtil.isValidOrThrow(json, JSON_SCHEMA);
            Match match = new Match();
            match.setMap(json.get("map").asText());
            match.setNsServerName(json.get("ns_server_name").asText());
            match.setGamemode(json.get("gamemode").asText());
            match.setOwner(requestDetails.getUUId().orElse(null));
            try {
                entityRepository.create(match);
            } catch (RollbackException ex) {
                throw new InternalLogicException(ex);
            }
            LOGGER.info("New match registered {}", match.getId());
            return parseEntityToResponse(match, Views.Public.class);
        };

        return super.restCall(restAction);
    }

    @PUT
    @Secured
    @Path(P_END + "/{id}")
    public Response endGame(@PathParam("id") String id) {
        RestAction restAction = () -> {
            if (StringUtils.isEmpty(id)) {
                return DefaultResponse.error(404, E_NOT_FOUND, requestDetails.getRequestContext());
            }
            try {
                Optional<Match> optMatch = entityRepository.find(UUID.fromString(id), Match.class);
                if (optMatch.isEmpty()) {
                    return DefaultResponse.error(404, E_NOT_FOUND, requestDetails.getRequestContext());
                }
                Match match = optMatch.get();
                match.setIsRunning(false);

                entityRepository.edit(match);
                globalGameState.removeGameState(match.getId());
                LOGGER.info("Match finished {}", match.getId());
                return parseEntityToResponse(match, Views.Public.class);
            } catch (RollbackException ex) {
                throw new InternalLogicException(ex);
            } catch (IllegalArgumentException ex) {
                return DefaultResponse.error(404, E_NOT_FOUND, requestDetails.getRequestContext());
            }
        };

        return super.restCall(restAction);
    }

    @GET
    @Path(P_PLAYING)
    public Response playing() {
        RestAction restAction = () -> {
            String result = JsonUtil.toJson(entityRepository.find(Q_ARE_PLAYING), Views.Public.class);
            return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.fromJson(result));
        };
        return super.restCall(restAction);
    }

    @GET
    @Path(P_STOPPED)
    public Response stopped() {
        RestAction restAction = () -> {
            String result = JsonUtil.toJson(entityRepository.find(Q_STOPPLED_PLAYING), Views.Public.class);
            return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.fromJson(result));
        };
        return super.restCall(restAction);
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