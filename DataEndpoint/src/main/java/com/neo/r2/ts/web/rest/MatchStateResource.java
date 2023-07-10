package com.neo.r2.ts.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.impl.exception.NoContentFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource {

    public static final String RESOURCE_LOCATION = "api/v1/matchstate";

    @Inject
    protected GlobalMatchState globalGameState;

    @GET
    @Path("/{id}")
    public JsonNode get(@PathParam("id") String id) {
        Optional<MatchStateWrapper> matchStateOptional = globalGameState.getCurrentMatchState(id);
        if (matchStateOptional.isPresent()) {
            return matchStateOptional.get().getState();
        }
        throw new NoContentFoundException(CustomConstants.EX_ALREADY_MATCH_ENDED);
    }
}
