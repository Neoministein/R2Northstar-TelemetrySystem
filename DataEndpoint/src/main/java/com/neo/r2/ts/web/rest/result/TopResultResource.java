package com.neo.r2.ts.web.rest.result;

import com.neo.r2.ts.impl.player.PlayerLookUpService;
import com.neo.r2.ts.impl.result.MatchResultRequestParam;
import com.neo.r2.ts.impl.result.MatchResultService;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.common.impl.MathUtils;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.framework.api.persistence.aggregation.AggregationResult;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;
import java.util.function.UnaryOperator;

@ApplicationScoped
@ClientCacheControl(maxAge = 60)
@Path(TopResultResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class TopResultResource {

    public static final String RESOURCE_LOCATION = "api/v1/result/top";

    @Inject
    protected PlayerLookUpService playerLookUpService;

    @Inject
    protected MatchResultService matchResultService;

    @Inject
    protected SearchProvider searchProvider;

    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public void createMatch(MatchResultDto matchResultDto) {
        if (searchProvider.enabled()) {
            if (matchResultDto.matchId().isPresent()) {
                saveResult(matchResultDto, searchable -> searchable);
            } else {
                String matchId = UUID.randomUUID().toString();
                saveResult(matchResultDto, searchable -> searchable.addMatchId(matchId));
            }
        }
        matchResultService.invalidateStatsCache();
    }

    protected void saveResult(MatchResultDto matchResultDto, UnaryOperator<MatchResultSearchable> matchId) {
        for (MatchResultDto.Player player : matchResultDto.players()) {
            searchProvider.index(matchId.apply(new MatchResultSearchable(matchResultDto, player)));
            playerLookUpService.updatePlayerLookUp(player.uId(), player.playerName());
        }
    }

    @GET
    @Path("npc-kills")
    public AggregationResult getNpcKills(@QueryParam("max") String maxResult, @QueryParam("page") String page,
                                         @QueryParam("tags") String tags) {
        return matchResultService.getNpcKills(parseMatchResultParm(tags, maxResult, page));
    }

    @GET
    @Path("player-kills")
    public AggregationResult getPlayerKills(@QueryParam("max") String maxResult, @QueryParam("page") String page,
                                            @QueryParam("tags") String tags) {
        return matchResultService.getPlayerKills(parseMatchResultParm(tags, maxResult, page));
    }

    @GET
    @Path("player-kd")
    public AggregationResult getPlayerKd(@QueryParam("max") String maxResult, @QueryParam("page") String page,
                                         @QueryParam("tags") String tags) {
        return matchResultService.getPlayerKd(parseMatchResultParm(tags, maxResult, page));
    }

    @GET
    @Path("win")
    public AggregationResult getWin(@QueryParam("max") String maxResult, @QueryParam("page") String page,
                                    @QueryParam("tags") String tags) {
        return matchResultService.getWin(parseMatchResultParm(tags, maxResult, page));
    }

    @GET
    @Path("win-ratio")
    public AggregationResult getWinRatio(@QueryParam("max") String maxResult, @QueryParam("page") String page,
                                         @QueryParam("tags") String tags) {
        return matchResultService.getWinRatio(parseMatchResultParm(tags, maxResult, page));
    }

    private MatchResultRequestParam parseMatchResultParm(String tags, String maxResult, String page) {
        return new MatchResultRequestParam(parseTags(tags), parseIntResult(maxResult, 100), parseIntResult(page, 1_000));
    }

    private String[] parseTags(String queryTags) {
        if (StringUtils.isEmpty(queryTags)) {
            return new String[0];
        }
        return queryTags.split(",");
    }

    private int parseIntResult(String queryMaxResult, int defaultVal) {
        try {
            return MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10_000);
        } catch (NumberFormatException ex) {
            return defaultVal;
        }
    }
}
