package com.neo.r2.ts.impl.repository.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.SearchableRepository;
import com.neo.r2.ts.impl.MethodNameCacheKeyGenerator;
import com.neo.r2.ts.impl.result.MatchResultRequestParam;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.cache.spi.CacheInvalidateAll;
import com.neo.util.framework.api.cache.spi.CacheResult;
import com.neo.util.framework.api.persistence.aggregation.*;
import com.neo.util.framework.api.persistence.criteria.ContainsSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import com.neo.util.framework.elastic.api.IndexNamingService;
import com.neo.util.framework.elastic.api.aggregation.BucketScriptAggregation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MatchResultRepository implements SearchableRepository {

    protected static final String MATCH_STATS_CACHE = "matchStatsCache";

    @Inject
    protected SearchProvider searchProvider;

    protected String indexName;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        indexName = indexNamingService.getIndexNamePrefixFromClass(MatchResultSearchable.class, true);
    }

    @CacheInvalidateAll(cacheName = MATCH_STATS_CACHE)
    public void saveResult(MatchResultSearchable matchResultSearchable) {
        searchProvider.index(matchResultSearchable);
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public List<JsonNode> getResultForMatch(String matchId) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setFields(List.of("gamemode", "uId", "hasWon", MatchResultSearchable.PGS_ELIMINATED, MatchResultSearchable.PGS_KILLS, MatchResultSearchable.PGS_DEATHS, MatchResultSearchable.PGS_PILOT_KILLS, MatchResultSearchable.PGS_TITAN_KILLS, MatchResultSearchable.PGS_NPC_KILLS, MatchResultSearchable.PGS_ASSISTS, MatchResultSearchable.PGS_SCORE, MatchResultSearchable.PGS_ASSAULT_SCORE, MatchResultSearchable.PGS_DEFENSE_SCORE, MatchResultSearchable.PGS_DISTANCE_SCORE, MatchResultSearchable.PGS_DETONATION_SCORE));
        searchQuery.addFilters(new ExplicitSearchCriteria("matchId", matchId));
        return searchProvider.fetch(indexName, searchQuery).getHits();
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getNpcKills(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("npc-kills","uId", param.maxResult(), new TermAggregation.Order(MatchResultSearchable.PGS_NPC_KILLS), null, List.of(new SimpleFieldAggregation(MatchResultSearchable.PGS_NPC_KILLS, MatchResultSearchable.PGS_NPC_KILLS, SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(indexName, searchQuery).getAggregations().get("npc-kills");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getPlayerKills(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("players-kills","uId", param.maxResult(),new TermAggregation.Order(MatchResultSearchable.PGS_PILOT_KILLS), null, List.of(new SimpleFieldAggregation(MatchResultSearchable.PGS_PILOT_KILLS, MatchResultSearchable.PGS_PILOT_KILLS, SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(indexName, searchQuery).getAggregations().get("players-kills");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getPlayerKd(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("kd", "params.kills / params.deaths", Map.of("kills", MatchResultSearchable.PGS_PILOT_KILLS, "deaths", MatchResultSearchable.PGS_DEATHS));
        searchQuery.setAggregations(List.of(new TermAggregation("players-kd","uId", param.maxResult(),new TermAggregation.Order("kd"), null, List.of(new SimpleFieldAggregation(MatchResultSearchable.PGS_PILOT_KILLS, MatchResultSearchable.PGS_PILOT_KILLS, SimpleFieldAggregation.Type.SUM), new SimpleFieldAggregation(MatchResultSearchable.PGS_DEATHS, MatchResultSearchable.PGS_DEATHS, SimpleFieldAggregation.Type.SUM), bucketScriptAggregation))));

        return searchProvider.fetch(indexName, searchQuery).getAggregations().get("players-kd");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getWin(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ExplicitSearchCriteria("hasWon",true), new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("win","uId", param.maxResult(), new TermAggregation.Order("win"), null, List.of(new SimpleFieldAggregation("win","matchId", SimpleFieldAggregation.Type.COUNT)))));

        return searchProvider.fetch(indexName, searchQuery).getAggregations().get("win");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getWinRatio(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("ratio", "params.wins / (params.wins + params.loses) * 100", Map.of("wins", "filters['win']>count", "loses", "filters['lose']>count"));
        searchQuery.setAggregations(List.of(new TermAggregation("win-ratio","uId", param.maxResult(), new TermAggregation.Order("ratio"),null, List.of(new CriteriaAggregation("filters", Map.of("win", new ExplicitSearchCriteria("hasWon",true),"lose", new ExplicitSearchCriteria("hasWon",false)), new SimpleFieldAggregation("count","matchId", SimpleFieldAggregation.Type.COUNT)), bucketScriptAggregation))));

        return searchProvider.fetch(indexName, searchQuery).getAggregations().get("win-ratio");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public int countMatches() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addAggregations(new SimpleFieldAggregation("distinctCount", MatchResultSearchable.MATCH_ID, SimpleFieldAggregation.Type.CARDINALITY));
        SearchResult<JsonNode> result = searchProvider.fetch(indexName, searchQuery);

        return ((SimpleAggregationResult) result.getAggregations().get("distinctCount")).getValue().intValue();
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public int countMatchesByMap(String map) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ExplicitSearchCriteria(MatchResultSearchable.MAP, map));
        searchQuery.addAggregations(new SimpleFieldAggregation("distinctCount", MatchResultSearchable.MATCH_ID, SimpleFieldAggregation.Type.CARDINALITY));
        SearchResult<JsonNode> result = searchProvider.fetch(indexName, searchQuery);

        return ((SimpleAggregationResult) result.getAggregations().get("distinctCount")).getValue().intValue();
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public int fetchUnqiueGameMode() { //TODO TEST
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addAggregations(new SimpleFieldAggregation("uniqueModes", MatchResultSearchable.GAMEMODE, SimpleFieldAggregation.Type.CARDINALITY));
        SearchResult<JsonNode> result = searchProvider.fetch(indexName, searchQuery);

        return ((SimpleAggregationResult) result.getAggregations().get("uniqueModes")).getValue().intValue();
    }

    @Override
    public String getIndexName() {
        return indexName;
    }
}
