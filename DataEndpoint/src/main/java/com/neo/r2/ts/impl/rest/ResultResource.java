package com.neo.r2.ts.impl.rest;

import com.neo.r2.ts.impl.persistence.searchable.MatchResultSearchable;
import com.neo.r2.ts.impl.persistence.searchable.PlayerUidSearchable;
import com.neo.r2.ts.impl.rest.dto.inbound.MatchResultDto;
import com.neo.util.common.impl.MathUtils;
import com.neo.util.framework.api.persistence.aggregation.*;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import com.neo.util.framework.elastic.api.IndexNamingService;
import com.neo.util.framework.elastic.api.aggregation.BucketScriptAggregation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path(ResultResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ResultResource {

    public static final String RESOURCE_LOCATION = "api/v1/result";

    @Inject
    protected SearchProvider searchProvider;

    protected String resultIndexName;
    protected String playerUid;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        resultIndexName = indexNamingService.getIndexNamePrefixFromClass(MatchResultSearchable.class, true);
        playerUid = indexNamingService.getIndexNamePrefixFromClass(PlayerUidSearchable.class, true);
    }

    @POST
    public void createMatch(MatchResultDto matchResultDto) {
        if (searchProvider.enabled()) {
            if (matchResultDto.matchId() != null) {
                searchProvider.index(matchResultDto.players().stream().map(player -> new MatchResultSearchable(matchResultDto, player)).toList());
            } else {
                String matchId = UUID.randomUUID().toString();
                searchProvider.index(matchResultDto.players().stream().map(player -> new MatchResultSearchable(matchResultDto, player).addMatchId(matchId)).toList());
            }

            searchProvider.update(matchResultDto.players().stream().map(player -> new PlayerUidSearchable(player.uId(), player.playerName())).toList(), true);
        }
    }

    @GET
    @Path("player/uid/{uid}")
    public Response getPlayerNameByUid(@PathParam("uid") String uid) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        cc.setPrivate(false);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setFields(List.of("playerName", "uId"));
        searchQuery.setFilters(List.of(new ExplicitSearchCriteria("_id",uid)));
        SearchResult result = searchProvider.fetch(playerUid,searchQuery);

        if (result.getHits().isEmpty()) {
            return Response.status(404).build();
        }


        return Response.ok().cacheControl(cc).entity(result.getHits().get(0)).build();
    }

    @GET
    @Path("player/name/{name}")
    public Response getUidByPlayerName(@PathParam("name") String name) {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        cc.setPrivate(false);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setFields(List.of("playerName", "uId"));
        searchQuery.setFilters(List.of(new ExplicitSearchCriteria("playerName", name)));
        SearchResult result = searchProvider.fetch(playerUid,searchQuery);

        if (result.getHits().isEmpty()) {
            return Response.status(404).build();
        }


        return Response.ok().cacheControl(cc).entity(result.getHits().get(0)).build();
    }

    @GET
    @Path("top/npc-kills")
    public AggregationResult getNpcKills(@QueryParam("max") @DefaultValue("10") String queryMaxResult) {
        int maxResult;
        try {
            maxResult = MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10000) ;
        } catch (NumberFormatException ex) {
            maxResult = 10;
        }

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setAggregations(List.of(new TermAggregation("npc-kills","uId", maxResult,new TermAggregation.Order("PGS_NPC_KILLS"), null, List.of(new SimpleFieldAggregation("PGS_NPC_KILLS", "PGS_NPC_KILLS", SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("npc-kills");
    }

    @GET
    @Path("top/player-kills")
    public AggregationResult getPLayerKills(@QueryParam("max") @DefaultValue("10") String queryMaxResult) {
        int maxResult;
        try {
            maxResult = MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10000) ;
        } catch (NumberFormatException ex) {
            maxResult = 10;
        }

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setAggregations(List.of(new TermAggregation("player-kills","uId", maxResult,new TermAggregation.Order("PGS_PILOT_KILLS"), null, List.of(new SimpleFieldAggregation("PGS_PILOT_KILLS", "PGS_PILOT_KILLS", SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("player-kills");
    }

    @GET
    @Path("top/player-kd")
    public AggregationResult getPLayerKd(@QueryParam("max") @DefaultValue("10") String queryMaxResult) {
        int maxResult;
        try {
            maxResult = MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10000) ;
        } catch (NumberFormatException ex) {
            maxResult = 10;
        }

        SearchQuery searchQuery = new SearchQuery();
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("kd", "params.kills / params.deaths", Map.of("kills", "PGS_PILOT_KILLS", "deaths", "PGS_DEATHS"));
        searchQuery.setAggregations(List.of(new TermAggregation("player-kd","uId", maxResult,new TermAggregation.Order("kd"), null, List.of(new SimpleFieldAggregation("PGS_PILOT_KILLS", "PGS_PILOT_KILLS", SimpleFieldAggregation.Type.SUM), new SimpleFieldAggregation("PGS_DEATHS", "PGS_DEATHS", SimpleFieldAggregation.Type.SUM), bucketScriptAggregation))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("player-kd");
    }

    @GET
    @Path("top/win")
    public AggregationResult getWin(@QueryParam("max") @DefaultValue("10") String queryMaxResult) {
        int maxResult;
        try {
            maxResult = MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10000) ;
        } catch (NumberFormatException ex) {
            maxResult = 10;
        }

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setFilters(List.of(new ExplicitSearchCriteria("hasWon",true)));
        searchQuery.setAggregations(List.of(new TermAggregation("win","uId", maxResult, new TermAggregation.Order("win"), null, List.of(new SimpleFieldAggregation("win","matchId", SimpleFieldAggregation.Type.COUNT)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("win");
    }

    @GET
    @Path("top/win-ratio")
    public AggregationResult getWinRatio(@QueryParam("max") @DefaultValue("10") String queryMaxResult) {
        int maxResult;
        try {
            maxResult = MathUtils.clamp(Integer.parseInt(queryMaxResult), 1, 10000) ;
        } catch (NumberFormatException ex) {
            maxResult = 10;
        }

        SearchQuery searchQuery = new SearchQuery();
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("ratio", "params.wins / (params.wins + params.looses) * 100", Map.of("wins", "filters['win']>count", "looses", "filters['loose']>count"));
        searchQuery.setAggregations(List.of(new TermAggregation("win-ratio","uId", maxResult, new TermAggregation.Order("ratio"),null, List.of(new CriteriaAggregation("filters", Map.of("win", new ExplicitSearchCriteria("hasWon",true),"loose", new ExplicitSearchCriteria("hasWon",false)), new SimpleFieldAggregation("count","matchId", SimpleFieldAggregation.Type.COUNT)), bucketScriptAggregation))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("win-ratio");
    }

    protected SearchQuery createSearchQuery(String queryMaxResult, String offset) {
        SearchQuery searchQuery = new SearchQuery();



        return searchQuery;
    }
}