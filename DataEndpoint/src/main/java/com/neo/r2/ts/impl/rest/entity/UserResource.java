package com.neo.r2.ts.impl.rest.entity;

import com.neo.r2.ts.impl.persistence.entity.UserToken;
import com.neo.r2.ts.impl.security.AuthenticationService;
import com.neo.r2.ts.impl.security.Secured;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.InternalJsonException;
import com.neo.util.framework.api.persitence.entity.EntityQuery;
import com.neo.util.framework.api.persitence.entity.EntityResult;
import com.neo.util.framework.rest.api.RestAction;
import com.neo.util.framework.rest.impl.DefaultResponse;
import com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Secured
    public Response create(String x) {
        authenticationService.init();
        return super.restCall(createAction(x), List.of(PERM_INTERNAL));
    }

    @GET
    @Secured
    @Path("/{owner}")
    public Response get(@PathParam("owner") String owner) {
        return super.restCall(getByValueAction(UserToken.C_OWNER, owner), List.of(PERM_INTERNAL));
    }

    @PUT
    @Secured
    public Response edit(String x) {
        authenticationService.init();
        return super.restCall(editAction(x), List.of(PERM_INTERNAL));
    }

    @GET
    @Path(P_INIT)
    public Response init() {
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
                return parseEntityToResponse(userToken, Views.Internal.class);
            }
            return DefaultResponse.error(403, E_FORBIDDEN, requestDetails.getRequestContext());
        };
        return super.restCall(restAction);
    }


    @Override
    protected Object convertToPrimaryKey(String s) {
        return Long.valueOf(s);
    }

    @Override
    protected Class<UserToken> getEntityClass() {
        return UserToken.class;
    }
}