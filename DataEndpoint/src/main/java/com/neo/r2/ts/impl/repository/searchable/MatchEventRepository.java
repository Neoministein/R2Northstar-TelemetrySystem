package com.neo.r2.ts.impl.repository.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.SearchableRepository;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.persistence.aggregation.SimpleAggregationResult;
import com.neo.util.framework.api.persistence.aggregation.SimpleFieldAggregation;
import com.neo.util.framework.api.persistence.criteria.DateSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import com.neo.util.framework.elastic.api.IndexNamingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class MatchEventRepository implements SearchableRepository {

    protected String indexName;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        indexName = indexNamingService.getIndexNamePrefixFromClass(MatchEventSearchable.class, true);
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    public int fetchTotal(String eventType, String field) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setMaxResults(0);
        searchQuery.addFilters(new ExplicitSearchCriteria(MatchEventSearchable.EVENT_TYPE, eventType));
        searchQuery.addAggregations(new SimpleFieldAggregation("sum", field, SimpleFieldAggregation.Type.SUM));

        SearchResult<JsonNode> result = searchProvider.fetch(indexName, searchQuery);
        return ((SimpleAggregationResult) result.getAggregations().get("sum")).getValue().intValue();
    }

    public int fetchTotal(String eventType, String field, Duration duration) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setMaxResults(0);
        searchQuery.addFilters(new DateSearchCriteria(MatchResultSearchable.TIME_STAMP, Instant.now().minus(duration), Instant.now()));
        searchQuery.addFilters(new ExplicitSearchCriteria(MatchEventSearchable.EVENT_TYPE, eventType));
        searchQuery.addAggregations(new SimpleFieldAggregation("sum", field, SimpleFieldAggregation.Type.SUM));

        SearchResult<JsonNode> result = searchProvider.fetch(indexName, searchQuery);
        return ((SimpleAggregationResult) result.getAggregations().get("sum")).getValue().intValue();
    }
}
