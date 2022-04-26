package com.neo.tf2.ms.impl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.impl.StopWatch;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.repository.SearchRepository;
import com.neo.tf2.ms.impl.persistence.searchable.MatchState;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.AbstractRestEndpoint;
import com.neo.util.javax.impl.rest.HttpMethod;
import com.neo.util.javax.impl.rest.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path(MatchStateResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchStateResource extends AbstractRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateResource.class);

    public static final String RESOURCE_LOCATION = "api/v1/matchState";

    @Inject
    SearchRepository searchRepository;

    @GET
    public Response get(String x) {
        RequestContext requestContext = getContext(HttpMethod.GET,"");
        RestAction restAction = () -> {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            JsonNode jsonNode = JsonUtil.fromJson(x);
            JsonSchemaUtil.isValidOrThrow(jsonNode, MatchState.JSON_SCHEMA);
            MatchState matchState = new MatchState(jsonNode.deepCopy());


            searchRepository.index(matchState);
            stopWatch.stop();
            LOGGER.error("Stopwatch {}",stopWatch.getElapsedTimeMs());
            return Response.ok().build();
        };

        return super.restCall(restAction,requestContext);
    }

    @Override
    protected String getClassURI() {
        return RESOURCE_LOCATION;
    }
}
