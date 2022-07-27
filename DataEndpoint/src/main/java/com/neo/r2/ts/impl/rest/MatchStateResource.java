package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.MatchStateService;
import com.neo.r2.ts.impl.match.GlobalMatchState;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchRepository;
import com.neo.util.framework.rest.api.parser.ValidateJsonSchema;
import com.neo.util.framework.rest.api.response.ResponseGenerator;
import com.neo.util.framework.rest.api.security.Secured;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource {

    public static final String RESOURCE_LOCATION = "api/v1/matchstate";

    @Inject
    protected SearchRepository searchRepository;

    @Inject
    protected ResponseGenerator responseGenerator;

    @Inject
    protected GlobalMatchState globalGameState;

    @Inject
    protected MatchStateService gameStateService;

    @Inject
    protected CustomRestRestResponse customRestRestResponse;

    @PUT
    @Secured
    @ValidateJsonSchema("schemas/MatchState.json")
    public Response put(JsonNode matchState) {
        gameStateService.updateGameState(matchState);
        return responseGenerator.success();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        try {
            JsonNode state = globalGameState.getCurrentMatchState(UUID.fromString(id));
            return responseGenerator.success(state);
        } catch (IllegalArgumentException ex) {
            return responseGenerator.error(404, "","");
        }
    }

    @GET
    @Path("/{id}/{offset}")
    public Response replayData(@PathParam("id") String id, @PathParam("offset") int offset) {
        if (searchRepository.enabled()) {
            SearchQuery searchQuery = new SearchQuery(100, List.of(new ExplicitSearchCriteria("matchId", id,false)));
            searchQuery.setFields(null);
            searchQuery.setOnlySource(true);
            searchQuery.setSorting(Map.of("timePassed",true));
            searchQuery.setOffset(offset);
            String searchResponse = JsonUtil.toJson(searchRepository.fetch("r2ts-match-state",searchQuery), Views.Public.class);
            return responseGenerator.success(JsonUtil.fromJson(searchResponse));
        }
        return responseGenerator.error(503, customRestRestResponse.getService());
    }
}
