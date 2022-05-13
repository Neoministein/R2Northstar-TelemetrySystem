package com.neo.tf2.ms.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.search.SearchRepository;
import com.neo.tf2.ms.impl.persistence.GlobalGameState;
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
import java.util.UUID;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource extends AbstractRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateResource.class);

    public static final String RESOURCE_LOCATION = "api/v1/matchstate";

    @Inject
    SearchRepository searchRepository;

    @Inject
    GlobalGameState globalGameState;

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
}
