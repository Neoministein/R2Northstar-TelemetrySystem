package com.neo.r2.ts.web.rest.result;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.repository.searchable.MatchResultRepository;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path(ResultResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ResultResource {

    public static final String RESOURCE_LOCATION = "api/v1/result";

    @Inject
    protected MatchResultRepository matchResultRepository;

    @GET
    @Path("/match/{id}")
    @ClientCacheControl(maxAge = 2, timeUnit = TimeUnit.HOURS)
    public HitsDto<JsonNode> request(@PathParam("id") String matchId) {
        return new HitsDto<>(matchResultRepository.getResultForMatch(matchId));
    }
}
