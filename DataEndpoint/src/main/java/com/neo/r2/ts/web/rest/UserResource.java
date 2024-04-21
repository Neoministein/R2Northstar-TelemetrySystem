package com.neo.r2.ts.web.rest;

import com.neo.r2.ts.impl.repository.entity.ApplicationRepository;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.r2.ts.web.rest.dto.GenericUserDto;
import com.neo.util.common.impl.exception.ExternalRuntimeException;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.framework.api.FrameworkConstants;
import com.neo.util.framework.rest.api.security.SecuredResource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint.EX_ENTITY_NOT_FOUND;
import static com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint.PERM_INTERNAL;

@RequestScoped
@Path(UserResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class UserResource {

    public static final String RESOURCE_LOCATION = "api/v1/user";

    public static final String P_INIT = "/init";


    protected final ApplicationRepository applicationUserRepository;

    @Inject
    public UserResource(ApplicationRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @POST
    @Path("{displayName}")
    @SecuredResource
    @RolesAllowed(PERM_INTERNAL)
    public GenericUserDto create(@PathParam("displayName") String displayName) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setDisplayName(displayName);

        return new GenericUserDto(applicationUser);
    }

    @PUT
    @SecuredResource
    @RolesAllowed(PERM_INTERNAL)
    @Transactional
    public GenericUserDto edit(GenericUserDto genericUserDto) {
        ApplicationUser user = applicationUserRepository.fetchByUid(genericUserDto.uId())
                .orElseThrow(() -> new ValidationException(EX_ENTITY_NOT_FOUND, genericUserDto.uId()));
        user.setDisplayName(genericUserDto.displayName());
        user.setDescription(genericUserDto.description());
        user.setDisabled(genericUserDto.disabled());
        return new GenericUserDto(user);
    }

    @PUT
    @Path("{uId}")
    @SecuredResource
    @RolesAllowed(PERM_INTERNAL)
    public Response remove(@PathParam("uId") String uId) {
        ApplicationUser user = applicationUserRepository.requestByUid(uId);
        applicationUserRepository.remove(user);
        return Response.ok().build();
    }

    @GET
    @Path(P_INIT)
    public GenericUserDto userInit() {
        if (applicationUserRepository.count() == 0) {
            ApplicationUser userToken = new ApplicationUser();
            userToken.setDisplayName("Admin");
            userToken.setDescription("Auto Generated Admin Token");
            userToken.getUserRoles().add(PERM_INTERNAL);
            applicationUserRepository.create(userToken);
            return new GenericUserDto(userToken);
        }
        throw new ExternalRuntimeException(FrameworkConstants.EX_FORBIDDEN);
    }
}