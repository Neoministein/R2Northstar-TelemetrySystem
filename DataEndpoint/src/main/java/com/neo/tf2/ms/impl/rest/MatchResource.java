package com.neo.tf2.ms.impl.rest;

import com.neo.common.api.action.Action;
import com.neo.common.api.json.Views;
import com.neo.common.impl.StringUtils;
import com.neo.common.impl.exception.InternalJsonException;
import com.neo.common.impl.exception.InternalLogicException;
import com.neo.common.impl.json.JsonUtil;
import com.neo.common.impl.lazy.LazyAction;
import com.neo.javax.api.persitence.repository.EntityRepository;
import com.neo.tf2.ms.impl.persistence.entity.Match;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.AbstractRestEndpoint;
import com.neo.util.javax.impl.rest.DefaultResponse;
import com.neo.util.javax.impl.rest.HttpMethod;
import com.neo.util.javax.impl.rest.RequestContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.RollbackException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
@Path(MatchResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchResource extends AbstractRestEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchResource.class);

    public static final String RESOURCE_LOCATION = "api/v1/match";

    public static final String P_NEW = "/new";
    public static final String P_END = "/end";

    protected static final JSONObject E_NOT_FOUND = DefaultResponse.errorObject("resources/000","Entity not found");
    protected static final JSONObject E_CANNOT_PARSE = DefaultResponse.errorObject("resources/001","Unable to retrieve entity");

    @Inject
    EntityRepository entityRepository;

    @POST
    @Path(P_NEW)
    public Response newGame(String x) {
        RequestContext requestContext = getContext(HttpMethod.POST, P_NEW);
        RestAction restAction = () -> {
            JSONObject data = new JSONObject(new JSONTokener(x));

            Action<Response> createEntity = () -> {
                Match game = new Match();
                game.setMap(data.getString("map"));
                game.setNsServerName("ns_server_name");
                try {
                    entityRepository.create(game);
                } catch (RollbackException ex) {
                    throw new InternalLogicException(ex);
                }
                return parseEntityToResponse(game, requestContext, Views.Public.class);
            };

            return LazyAction.call(createEntity,3);
        };

        return super.restCall(restAction, requestContext);
    }

    @PUT
    @Path(P_END + "/{id}")
    public Response endGame(@PathParam("id") String id) {
        RequestContext requestContext = getContext(HttpMethod.PUT, P_END + "/" + id);
        RestAction restAction = () -> {
            if (StringUtils.isEmpty(id)) {
                return DefaultResponse.error(404, E_NOT_FOUND, requestContext);
            }
            try {
                Optional<Match> optGame = entityRepository.find(UUID.fromString(id), Match.class);
                if (optGame.isEmpty()) {
                    return DefaultResponse.error(404, E_NOT_FOUND, requestContext);
                }
                Match game = optGame.get();
                game.setRunning(false);

                entityRepository.edit(game);
                return parseEntityToResponse(game, requestContext, Views.Public.class);
            } catch (RollbackException ex) {
                throw new InternalLogicException(ex);
            } catch (IllegalArgumentException ex) {
                return DefaultResponse.error(404, E_NOT_FOUND, getContext(HttpMethod.PUT, P_END + "/" + id));
            }
        };

        return super.restCall(restAction, requestContext);
    }

    @Override
    protected String getClassURI() {
        return RESOURCE_LOCATION;
    }

    protected Response parseEntityToResponse(Match entity, RequestContext requestContext, Class<?> serializationScope) {
        try {
            String result = JsonUtil.toJson(entity, serializationScope);
            return DefaultResponse
                    .success(requestContext, new JSONArray().put(new JSONObject(new JSONTokener(result))));
        } catch (JSONException | InternalJsonException ex) {
            LOGGER.error("Unable to parse database entity to JSON {}", ex.getMessage());
            return DefaultResponse.error(500, E_CANNOT_PARSE, requestContext);
        }
    }
}
