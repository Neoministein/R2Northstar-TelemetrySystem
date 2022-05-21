package com.neo.tf2.ms.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.api.json.Views;
import com.neo.common.impl.exception.InternalLogicException;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.search.SearchQuery;
import com.neo.javax.api.persitence.search.SearchRepository;
import com.neo.tf2.ms.impl.heatmap.HeatmapImpl;
import com.neo.tf2.ms.impl.persistence.GlobalGameState;
import com.neo.tf2.ms.impl.persistence.searchable.MatchEvent;
import com.neo.tf2.ms.impl.persistence.searchable.MatchState;
import com.neo.tf2.ms.impl.security.Secured;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.AbstractRestEndpoint;
import com.neo.util.javax.impl.rest.DefaultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource extends AbstractRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateResource.class);

    public static final ObjectNode E_SERVICE = DefaultResponse.errorObject("svc/000", "Internal service not available");

    public static final String RESOURCE_LOCATION = "api/v1/matchstate";

    @Inject
    SearchRepository searchRepository;

    @Inject
    GlobalGameState globalGameState;

    @Inject
    HeatmapImpl heatmap;

    @PUT
    @Secured
    public Response put(String x) {
        RestAction restAction = () -> {
            JsonNode jsonNode = JsonUtil.fromJson(x);
            JsonSchemaUtil.isValidOrThrow(jsonNode, MatchState.JSON_SCHEMA);
            globalGameState.setCurrentMatchState(jsonNode);
            if (searchRepository.enabled()) {
                MatchState matchState = new MatchState(jsonNode.deepCopy());
                searchRepository.index(matchState);
                searchRepository.index(parseStateToEvents(jsonNode));
            }
            return DefaultResponse.success(requestDetails.getRequestContext());
        };

        return super.restCall(restAction);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        RestAction restAction = () -> {
            JsonNode state = globalGameState.getCurrentMatchState(UUID.fromString(id));
            return DefaultResponse.success(requestDetails.getRequestContext(), state);
        };
        return super.restCall(restAction);
    }

    @GET
    @Path("/{id}/{offset}")
    public Response replayData(@PathParam("id") String id, @PathParam("offset") int offset) {
        RestAction restAction = () -> {
            if (searchRepository.enabled()) {
                SearchQuery searchQuery = new SearchQuery(100, List.of(new ExplicitSearchCriteria("matchId", id,false)));
                searchQuery.setFields(null);
                searchQuery.setOnlySource(true);
                searchQuery.setSorting(Map.of("timePassed",true));
                searchQuery.setOffset(offset);
                String searchResponse = JsonUtil.toJson(searchRepository.fetch("tfms-match-state-*",searchQuery), Views.Public.class);
                return DefaultResponse.success(requestDetails.getRequestContext(), JsonUtil.fromJson(searchResponse));
            }
            return DefaultResponse.error(503, E_SERVICE,requestDetails.getRequestContext());
        };
        return super.restCall(restAction);
    }

    @GET
    @Path("map/{map}")
    public Response heatmapData(@PathParam("map") String map) {
        RestAction restAction = () -> {
            try {
                heatmap.calculate(map);
                return DefaultResponse.success(requestDetails.getRequestContext());
            } catch (InternalLogicException ex) {
                return DefaultResponse.error(503, E_SERVICE,requestDetails.getRequestContext());
            }
        };
        return super.restCall(restAction);
    }

    protected List<MatchEvent> parseStateToEvents(JsonNode state) {
        Map<String, JsonNode> players = new HashMap<>();
        List<MatchEvent> matchEventList = new ArrayList<>();
        for (JsonNode p: state.get(MatchState.F_PLAYERS)) {
            ObjectNode player = p.deepCopy();
            player.put(MatchEvent.F_IS_PLAYER,false);
            players.put(player.get(MatchState.F_PLAYER_ID).asText(), player);
            MatchEvent matchEvent = new MatchEvent(state, MatchEvent.T_POSTION);
            matchEvent.setEntity(player);
            matchEventList.add(matchEvent);
        }
        List<String> basicEvents = List.of(
                MatchState.F_CONNECT,
                MatchState.F_DISCONNECT,
                MatchState.F_RESPAWNED,
                MatchState.F_PILOT_BECOMES_TITAN,
                MatchState.F_TITAN_BECOMES_PILOT,
                MatchState.F_JUMP,
                MatchState.F_DOUBLE_JUMP,
                MatchState.F_MANTLE,
                MatchState.F_NEW_LOADOUT);
        addBasicEvents(basicEvents,players, state,matchEventList);

        for (JsonNode event: state.get(MatchState.F_EVENTS).get(MatchState.F_KILLED)) {
            MatchEvent matchEvent = new MatchEvent(state, MatchState.F_KILLED);
            matchEvent.setEntity(players.get(event.get(MatchState.F_PLAYER_ID).asText()));
            ObjectNode data = JsonUtil.emptyObjectNode();
            data.set(MatchEvent.F_VICTIM, players.get(event.get(MatchState.F_VICTIM).asText()));
            data.put(MatchEvent.F_DAMAGE_TYPE, event.get(MatchState.F_WEAPON).asText());
            matchEventList.add(matchEvent);
        }
        return matchEventList;
    }

    public void addBasicEvents(List<String> basicEvents, Map<String, JsonNode> players, JsonNode state, List<MatchEvent> toAddTo) {
        for (String basicEvent : basicEvents) {
            for (JsonNode event: state.get(MatchState.F_EVENTS).get(basicEvent)) {
                MatchEvent matchEvent = new MatchEvent(state, basicEvent);
                matchEvent.setEntity(players.get(event.get(MatchState.F_PLAYER_ID).asText()));
                toAddTo.add(matchEvent);
            }
        }
    }
}
