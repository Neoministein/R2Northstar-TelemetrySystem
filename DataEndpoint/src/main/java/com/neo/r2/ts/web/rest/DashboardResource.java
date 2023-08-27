package com.neo.r2.ts.web.rest;

import com.neo.r2.ts.impl.match.event.processor.player.movement.PlayerPositionEventProcessor;
import com.neo.r2.ts.impl.match.result.MatchResultService;
import com.neo.r2.ts.impl.repository.searchable.MatchEventRepository;
import com.neo.r2.ts.impl.repository.searchable.MatchResultRepository;
import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.r2.ts.web.rest.dto.outbound.ValueDto;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequestScoped
@Path(DashboardResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@ClientCacheControl(timeUnit = TimeUnit.MINUTES, maxAge = 1)
public class DashboardResource {

    public static final String RESOURCE_LOCATION = "api/v1/dashboard";

    @Inject
    protected PlayerLookUpRepository playerLookUpRepository;

    @Inject
    protected MatchResultService matchResultService;

    @Inject
    protected MatchResultRepository matchResultRepository;

    @Inject
    protected MatchEventRepository matchEventRepository;

    @GET
    @Path("/unqiue/player")
    public ValueDto uniquePlayers(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {
        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(playerLookUpRepository.countUniquePlayers(optionalDuration.get()));
        }
        return new ValueDto(playerLookUpRepository.countUniquePlayers());
    }

    @GET
    @Path("/mode/distribution")
    public HitsDto getGamemodeDistribution() {
        return new HitsDto(matchResultService.getGamemodeDistribution());
    }

    @GET
    @Path("/total/player-kills")
    public ValueDto getTotalPlayerKills(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {
        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(matchResultRepository.fetchTotal(MatchResultSearchable.PGS_PILOT_KILLS, optionalDuration.get()));
        }
        return new ValueDto(matchResultRepository.fetchTotal(MatchResultSearchable.PGS_PILOT_KILLS));
    }

    @GET
    @Path("/total/npc-kills")
    public ValueDto getTotalNpcKills(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {
        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(matchResultRepository.fetchTotal(MatchResultSearchable.PGS_NPC_KILLS, optionalDuration.get()));
        }
        return new ValueDto(matchResultRepository.fetchTotal(MatchResultSearchable.PGS_NPC_KILLS));
    }

    @GET
    @Path("/matches")
    public ValueDto getTotalMatches(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {
        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(matchResultRepository.countTotalMatches(optionalDuration.get()));
        }
        return new ValueDto(matchResultRepository.countMatches());
    }

    @GET
    @Path("/total/player-distance")
    public ValueDto getTotalDistanceTraveled(@QueryParam("time") String time, @QueryParam("timeunit") String timeUnit) {
        Optional<Duration> optionalDuration = FrameworkMapping.parseDuration(timeUnit, time);

        if (optionalDuration.isPresent()) {
            return new ValueDto(matchEventRepository.fetchTotal(PlayerPositionEventProcessor.EVENT_NAME, "entity.distance", optionalDuration.get()));
        }

        return new ValueDto(matchEventRepository.fetchTotal(PlayerPositionEventProcessor.EVENT_NAME, "entity.distance"));
    }
}
