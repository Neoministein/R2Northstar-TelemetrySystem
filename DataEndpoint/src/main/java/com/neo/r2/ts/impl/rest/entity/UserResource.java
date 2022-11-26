package com.neo.r2.ts.impl.rest.entity;

import com.neo.r2.ts.impl.persistence.entity.UserToken;
import com.neo.r2.ts.impl.persistence.repository.UserTokenRepository;
import com.neo.util.common.api.json.Views;
import com.neo.util.framework.api.FrameworkConstants;
import com.neo.util.framework.rest.api.security.Secured;
import com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path(UserResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class UserResource extends AbstractEntityRestEndpoint<UserToken> {

    public static final String RESOURCE_LOCATION = "api/v1/user";

    public static final String P_INIT = "/init";

    @Inject
    protected UserTokenRepository userTokenRepository;

    @POST
    @Secured
    @Override
    @RolesAllowed(PERM_INTERNAL)
    public Response create(String x) {
        return super.create(x);
    }

    @PUT
    @Secured
    @RolesAllowed(PERM_INTERNAL)
    @Override
    public Response edit(String x) {
        return super.edit((x));
    }

    @GET
    @Path(P_INIT)
    public Response userInit() {
        if (userTokenRepository.count() == 0) {
            UserToken userToken = new UserToken();
            userToken.setDescription("Admin token");
            userToken.getRoles().add(PERM_INTERNAL);
            entityRepository.create(userToken);
            return parseToResponse(userToken, Views.Internal.class);
        }
        return responseGenerator.error(403, FrameworkConstants.EX_FORBIDDEN);
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