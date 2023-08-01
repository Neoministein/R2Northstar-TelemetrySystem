package com.neo.r2.ts.web.rest;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.match.MatchService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.r2.ts.web.rest.dto.inbound.NewMatchDto;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.r2.ts.web.rest.dto.outbound.MatchDto;
import com.neo.util.common.api.json.Views;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.framework.api.request.RequestDetails;
import com.neo.util.framework.rest.api.parser.OutboundJsonView;
import com.neo.util.framework.rest.api.request.HttpRequestDetails;
import com.neo.util.framework.rest.api.security.Secured;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RequestScoped
@Path(MatchResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class MatchResource {

    public static final String RESOURCE_LOCATION = "api/v1/match";

    public static final String P_NEW = "/new";
    public static final String P_END = "/end";

    public static final String P_PLAYING = "/playing";
    public static final String P_STOPPED = "/stopped";

    public static final String P_HEATMAP = "/heatmap";

    @Inject
    protected MatchService matchService;

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected MatchRepository matchRepository;

    @Inject
    protected RequestDetails requestDetails;

    @GET
    @Path("/{id}")
    @OutboundJsonView(Views.Public.class)
    public Match requestMatchById(@PathParam("id") String id) {
        return matchRepository.fetch(id).orElseThrow(() ->
                new NoContentFoundException(CustomConstants.EX_MATCH_NON_EXISTENT, id));
    }

    @POST
    @Secured
    @Path(P_NEW)
    @OutboundJsonView(Views.Public.class)
    public Match createMatch(NewMatchDto newMatchDto) {
        return matchService.createMatch(
                newMatchDto,
                (ApplicationUser) ((HttpRequestDetails) requestDetails).getUser().orElseThrow());
    }

    @PUT
    @Secured
    @Path(P_END + "/{id}")
    @OutboundJsonView(Views.Public.class)
    @Transactional
    public Match endMatch(@PathParam("id") String id) {
        Match match = requestMatchById(id);

        if (!match.getIsRunning()) {
            throw new ValidationException(CustomConstants.EX_ALREADY_MATCH_ENDED, id);
        }

        return matchService.endMatch(match);
    }

    @GET
    @Path(P_PLAYING)
    @Transactional
    @OutboundJsonView(Views.Public.class)
    public HitsDto playing() {
        List<MatchDto> matchDtoList = matchRepository.fetchArePlaying().stream()
                .map(match -> new MatchDto(match, matchStateService.getNumberOfPlayerInMatch(match.getId()))).toList();

        return new HitsDto(matchDtoList);
    }

    @GET
    @Path(P_STOPPED)
    @Transactional
    @OutboundJsonView(Views.Public.class)
    public HitsDto stopped() {
        return new HitsDto(matchRepository.fetchStoppedPlaying());
    }

    @GET
    @Path("/{id}" + P_HEATMAP)
    @Transactional
    @OutboundJsonView(Views.Public.class)
    public Heatmap getHeatmap(@PathParam("id") String id) {
        List<Heatmap> heatmaps = requestMatchById(id).getHeatmaps();
        if (heatmaps.isEmpty()) {
            throw new NoContentFoundException(CustomConstants.EX_NO_HEATMAP_FOR_MATCH, id);
        }
        return heatmaps.get(0);
    }
}
