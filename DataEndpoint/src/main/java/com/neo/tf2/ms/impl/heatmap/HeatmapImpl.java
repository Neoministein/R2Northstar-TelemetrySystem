package com.neo.tf2.ms.impl.heatmap;

import com.neo.common.impl.exception.InternalJsonException;
import com.neo.javax.api.persitence.aggregation.SearchAggregation;
import com.neo.javax.api.persitence.aggregation.SimpleAggregationResult;
import com.neo.javax.api.persitence.aggregation.SimpleFieldAggregation;
import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.criteria.SearchCriteria;
import com.neo.javax.api.persitence.search.SearchQuery;
import com.neo.javax.api.persitence.search.SearchRepository;
import com.neo.javax.api.persitence.search.SearchResult;
import com.neo.tf2.ms.impl.persistence.searchable.MatchEvent;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class HeatmapImpl {

    @Inject
    SearchRepository searchRepository;

    public void calculate(String map) {
        if (searchRepository.enabled()) {
            Map<String, Integer> resultMap = new HashMap<>();

            SearchCriteria mapFilter = new ExplicitSearchCriteria("map", map);
            SearchAggregation countAggregation = new SimpleFieldAggregation("count", MatchEvent.F_MATCH_ID, SearchAggregation.AggregationType.COUNT);
            int[] bounds = getMapBounds(mapFilter);
            for (int x = bounds[0]; x < bounds[1]; x++) {
                for (int y = bounds[2]; y < bounds[3]; y++) {
                    SearchQuery searchQuery = new SearchQuery(0);
                    searchQuery.setFilters(List.of(
                            mapFilter,
                            new ExplicitSearchCriteria("players.position.x",x),
                            new ExplicitSearchCriteria("players.position.y", y)));
                    searchQuery.setAggregations(List.of(countAggregation));

                    SearchResult searchResult = searchRepository.fetch("tfms-match-event",searchQuery);
                    resultMap.put(x + "|" + y, getIntFromResult(searchResult, "count"));
                }
            }
            System.out.println();
            return;

        }
        throw new InternalJsonException("A");
    }

    protected int[] getMapBounds(SearchCriteria mapName) {
        int[] bounds = new int[4];
        SearchQuery searchQuery = new SearchQuery(0);
        searchQuery.setFilters(List.of(mapName));
        searchQuery.setAggregations(List.of(
                        new SimpleFieldAggregation("xMin","players.position.x", SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("xMax","players.position.x", SearchAggregation.AggregationType.MAX),
                        new SimpleFieldAggregation("yMin","players.position.y", SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("yMax","players.position.y", SearchAggregation.AggregationType.MAX)));

        SearchResult searchResult = searchRepository.fetch("tfms-match-event",searchQuery);
        bounds[0] = getIntFromResult(searchResult,"xMin");
        bounds[1] = getIntFromResult(searchResult,"xMax");
        bounds[2] = getIntFromResult(searchResult,"yMin");
        bounds[3] = getIntFromResult(searchResult,"yMax");

        return bounds;
    }

    protected int getIntFromResult(SearchResult searchResult, String name) {
        return (int) Math.round((double) ((SimpleAggregationResult) searchResult.getAggregations().get(name)).getValue());
    }
}
