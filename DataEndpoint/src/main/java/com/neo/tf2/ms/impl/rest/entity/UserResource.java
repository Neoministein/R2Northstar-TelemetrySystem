package com.neo.tf2.ms.impl.rest.entity;

import com.neo.common.api.json.Views;
import com.neo.common.impl.exception.InternalJsonException;
import com.neo.javax.api.persitence.entity.EntityQuery;
import com.neo.javax.api.persitence.entity.EntityResult;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;
import com.neo.tf2.ms.impl.security.Secured;
import com.neo.util.javax.api.rest.RestAction;
import com.neo.util.javax.impl.rest.DefaultResponse;
import com.neo.util.javax.impl.rest.HttpMethod;
import com.neo.util.javax.impl.rest.RequestContext;
import com.neo.util.javax.impl.rest.entity.AbstractEntityRestEndpoint;

import javax.enterprise.context.RequestScoped;
import javax.transaction.RollbackException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path(UserResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class UserResource extends AbstractEntityRestEndpoint<UserToken> {

    public static final String RESOURCE_LOCATION = "api/v1/user";

    public static final String P_INIT = "/init";

    @POST
    @Secured
    public Response create(String x) {
        RequestContext requestContext = new RequestContext(HttpMethod.POST, getClassURI(), "");
        return super.restCall(create(x, requestContext), requestContext, List.of(PERM_INTERNAL));
    }

    @GET
    @Secured
    @Path("/{owner}")
    public Response get(@PathParam("owner") String owner) {
        RequestContext requestContext = new RequestContext(HttpMethod.GET, getClassURI(), "");
        return super.restCall(getByValue(UserToken.C_OWNER, owner, requestContext), requestContext, List.of(PERM_INTERNAL));
    }

    @PUT
    @Secured
    @Path("/{owner}")
    public Response edit(@PathParam("owner") String owner, String x) {
        RequestContext requestContext = new RequestContext(HttpMethod.PUT, getClassURI(), "");
        return super.restCall(edit(x, requestContext), requestContext, List.of(PERM_INTERNAL));
    }

    @DELETE
    @Secured
    public Response delete(String x) {
        RequestContext requestContext = new RequestContext(HttpMethod.POST, getClassURI(), "");
        return super.restCall(delete(x, requestContext), requestContext, List.of(PERM_INTERNAL));
    }

    @POST
    @Path(P_INIT)
    public Response init() {
        RequestContext requestContext = new RequestContext(HttpMethod.POST, getClassURI(), P_INIT);
        RestAction restAction = () -> {
            EntityResult<UserToken> result = entityRepository.find(new EntityQuery<>(UserToken.class, 0, List.of()));
            if (result.getHitSize() == 0) {
                UserToken userToken = new UserToken();
                userToken.setDescription("Admin token");
                userToken.getRoles().add(PERM_INTERNAL);
                try {
                    entityRepository.create(userToken);
                } catch (RollbackException e) {
                    throw new InternalJsonException("Unable to create initial user");
                }
                return parseEntityToResponse(userToken, requestContext, Views.Internal.class);
            }
            return DefaultResponse.error(403, E_FORBIDDEN, requestContext);
        };
        return super.restCall(restAction,requestContext);
    }


    @Override
    protected Object convertToPrimaryKey(String s) {
        return Long.valueOf(s);
    }

    @Override
    protected Class<UserToken> getEntityClass() {
        return UserToken.class;
    }

    @Override
    protected String getClassURI() {
        return RESOURCE_LOCATION;
    }
}