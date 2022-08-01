package com.neo.r2.ts.impl.rest.entity;

import com.neo.r2.ts.impl.persistence.entity.UserToken;
import com.neo.r2.ts.impl.rest.CustomRestRestResponse;
import com.neo.r2.ts.impl.security.BasicAuthenticationProvider;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.InternalJsonException;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityResult;
import com.neo.util.framework.rest.api.security.Secured;
import com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path(UserResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class UserResource extends AbstractEntityRestEndpoint<UserToken> {

    public static final String RESOURCE_LOCATION = "api/v1/user";

    public static final String P_INIT = "/init";

    @Inject BasicAuthenticationProvider authenticationService;

    @Inject
    CustomRestRestResponse customRestRestResponse;

    @POST
    @Secured
    @Override
    public Response create(String x) {
        return super.create(x);
    }

    @GET
    @Secured
    @Path("/{owner}")
    @RolesAllowed(PERM_INTERNAL)
    public Response get(@PathParam("owner") String owner) {
        return super.getByValue(UserToken.C_OWNER, owner);
    }

    @PUT
    @Secured
    @RolesAllowed(PERM_INTERNAL)
    @Override
    public Response edit(String x) {
        authenticationService.init();
        return super.edit((x));
    }

    @GET
    @Path(P_INIT)
    public Response userInit() {
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
        return responseGenerator.error(403,customRestRestResponse.getForbidden());
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