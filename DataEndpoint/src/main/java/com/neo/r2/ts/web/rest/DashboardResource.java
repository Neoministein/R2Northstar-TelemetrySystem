package com.neo.r2.ts.web.rest;

import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.r2.ts.web.rest.dto.outbound.ValueDto;
import com.neo.util.framework.api.FrameworkMapping;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.Optional;

@RequestScoped
@Path(DashboardResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DashboardResource {

    public static final String RESOURCE_LOCATION = "api/v1/dashboard";

    @Inject
    protected PlayerLookUpRepository playerLookUpRepository;

    @GET
    @Path("/unqiue/player")
    public ValueDto uniquePlayers(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {

        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(playerLookUpRepository.countUniquePlayers(optionalDuration.get()));
        }
        return new ValueDto(playerLookUpRepository.countUniquePlayers());
    }
}
