package com.neo.r2.ts.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.ConfigurationException;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import com.neo.util.framework.rest.api.parser.OutboundJsonView;
import com.neo.util.framework.rest.api.parser.ValidateJsonSchema;
import com.neo.util.framework.rest.api.security.Secured;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource {

    public static final String RESOURCE_LOCATION = "api/v1/matchstate";

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected GlobalMatchState globalGameState;

    @Inject
    protected MatchStateService gameStateService;

    @PUT
    @Secured
    @ValidateJsonSchema("MatchState.json")
    public Response put(JsonNode matchState) {
        gameStateService.updateGameState(matchState);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public JsonNode get(@PathParam("id") String id) {
        try {
            Optional<MatchStateWrapper> matchStateOptional = globalGameState.getCurrentMatchState(UUID.fromString(id));
            if (matchStateOptional.isPresent()) {
                return matchStateOptional.get().getJson();
            }
        } catch (IllegalArgumentException ignored) {
            //Happens when UUID is invalid 
        }
        throw new NoContentFoundException(CustomConstants.EX_ALREADY_MATCH_ENDED);
    }

    @GET
    @Path("/{id}/{offset}")
    @OutboundJsonView(Views.Public.class)
    public SearchResult replayData(@PathParam("id") String id, @PathParam("offset") int offset) {
        if (searchProvider.enabled()) {
            SearchQuery searchQuery = new SearchQuery(100, List.of(new ExplicitSearchCriteria("matchId", id,false)));
            searchQuery.setFields(null);
            searchQuery.setOnlySource(true);
            searchQuery.setSorting(Map.of("timePassed",true));
            searchQuery.setOffset(offset);
            return searchProvider.fetch("r2ts-match-state",searchQuery);
        }
        throw new ConfigurationException(CustomConstants.EX_SERVICE_UNAVAILABLE);
    }
}
