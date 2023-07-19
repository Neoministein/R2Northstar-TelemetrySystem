package com.neo.r2.ts.web.rest.result;

import com.neo.r2.ts.impl.result.MatchResultService;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@ClientCacheControl(maxAge = 60)
@Path(ResultResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ResultResource {

    public static final String RESOURCE_LOCATION = "api/v1/result";

    @Inject
    protected MatchResultService matchResultService;

    @GET
    @Path("/match/{id}")
    public HitsDto request(@PathParam("id") String matchId) {
        return new HitsDto(matchResultService.getResultForMatch(matchId));
    }
}
