package com.neo.r2.ts.impl.result;

import com.neo.r2.ts.impl.MethodNameCacheKeyGenerator;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.cache.spi.CacheInvalidateAll;
import com.neo.util.framework.api.cache.spi.CacheResult;
import com.neo.util.framework.api.persistence.aggregation.AggregationResult;
import com.neo.util.framework.api.persistence.aggregation.CriteriaAggregation;
import com.neo.util.framework.api.persistence.aggregation.SimpleFieldAggregation;
import com.neo.util.framework.api.persistence.aggregation.TermAggregation;
import com.neo.util.framework.api.persistence.criteria.ContainsSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.elastic.api.IndexNamingService;
import com.neo.util.framework.elastic.api.aggregation.BucketScriptAggregation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MatchResultService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MatchResultService.class);

    protected static final String MATCH_STATS_CACHE = "matchStatsCache";

    @Inject
    protected SearchProvider searchProvider;

    protected String resultIndexName;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        resultIndexName = indexNamingService.getIndexNamePrefixFromClass(MatchResultSearchable.class, true);
    }

    @CacheInvalidateAll(cacheName = MATCH_STATS_CACHE)
    public void invalidateStatsCache() {
        LOGGER.info("Invalidating stats cache");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getNpcKills(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("npc-kills","uId", param.maxResult(),new TermAggregation.Order("PGS_NPC_KILLS"), null, List.of(new SimpleFieldAggregation("PGS_NPC_KILLS", "PGS_NPC_KILLS", SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("npc-kills");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getPlayerKills(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("players-kills","uId", param.maxResult(),new TermAggregation.Order("PGS_PILOT_KILLS"), null, List.of(new SimpleFieldAggregation("PGS_PILOT_KILLS", "PGS_PILOT_KILLS", SimpleFieldAggregation.Type.SUM)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("players-kills");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getPlayerKd(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("kd", "params.kills / params.deaths", Map.of("kills", "PGS_PILOT_KILLS", "deaths", "PGS_DEATHS"));
        searchQuery.setAggregations(List.of(new TermAggregation("players-kd","uId", param.maxResult(),new TermAggregation.Order("kd"), null, List.of(new SimpleFieldAggregation("PGS_PILOT_KILLS", "PGS_PILOT_KILLS", SimpleFieldAggregation.Type.SUM), new SimpleFieldAggregation("PGS_DEATHS", "PGS_DEATHS", SimpleFieldAggregation.Type.SUM), bucketScriptAggregation))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("players-kd");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getWin(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ExplicitSearchCriteria("hasWon",true), new ContainsSearchCriteria("tags", param.tags()));
        searchQuery.setAggregations(List.of(new TermAggregation("win","uId", param.maxResult(), new TermAggregation.Order("win"), null, List.of(new SimpleFieldAggregation("win","matchId", SimpleFieldAggregation.Type.COUNT)))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("win");
    }

    @CacheResult(cacheName = MATCH_STATS_CACHE, keyGenerator = MethodNameCacheKeyGenerator.class)
    public AggregationResult getWinRatio(MatchResultRequestParam param) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.addFilters(new ContainsSearchCriteria("tags", param.tags()));
        BucketScriptAggregation bucketScriptAggregation = new BucketScriptAggregation("ratio", "params.wins / (params.wins + params.loses) * 100", Map.of("wins", "filters['win']>count", "loses", "filters['lose']>count"));
        searchQuery.setAggregations(List.of(new TermAggregation("win-ratio","uId", param.maxResult(), new TermAggregation.Order("ratio"),null, List.of(new CriteriaAggregation("filters", Map.of("win", new ExplicitSearchCriteria("hasWon",true),"lose", new ExplicitSearchCriteria("hasWon",false)), new SimpleFieldAggregation("count","matchId", SimpleFieldAggregation.Type.COUNT)), bucketScriptAggregation))));

        return searchProvider.fetch(resultIndexName, searchQuery).getAggregations().get("win-ratio");
    }
}
