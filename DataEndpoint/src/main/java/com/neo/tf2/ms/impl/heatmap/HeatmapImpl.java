package com.neo.tf2.ms.impl.heatmap;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.impl.exception.InternalJsonException;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.aggregation.SearchAggregation;
import com.neo.javax.api.persitence.aggregation.SimpleAggregationResult;
import com.neo.javax.api.persitence.aggregation.SimpleFieldAggregation;
import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.criteria.LongRangeSearchCriteria;
import com.neo.javax.api.persitence.criteria.SearchCriteria;
import com.neo.javax.api.persitence.search.SearchQuery;
import com.neo.javax.api.persitence.search.SearchRepository;
import com.neo.javax.api.persitence.search.SearchResult;
import com.neo.tf2.ms.impl.minimap.MapScale;
import com.neo.tf2.ms.impl.minimap.MapScalingService;
import com.neo.tf2.ms.impl.persistence.searchable.MatchEvent;
import com.neo.tf2.ms.impl.rest.MatchStateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class HeatmapImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateResource.class);

    private static final SearchAggregation COUNT_AGGREGATION = new SimpleFieldAggregation("count", MatchEvent.F_MATCH_ID, SearchAggregation.AggregationType.COUNT);

    private static final int PIXELS_PER_CALL = 4;

    private Map<String, ObjectNode> heatmaps = new HashMap<>();

    @Inject
    SearchRepository searchRepository;

    @Inject
    MapScalingService mapScalingService;

    public ObjectNode calculate(String map) {
        if (heatmaps.containsKey(map)) {
            return heatmaps.get(map);
        }
        if (searchRepository.enabled()) {
            long highestCount = 0;
            ObjectNode result =  JsonUtil.emptyObjectNode();
            ArrayNode resultArray = JsonUtil.emptyArrayNode();
            result.set("entries", resultArray);

            MapScale mapScale = mapScalingService.getMapScale(map);

            SearchCriteria mapFilter = new ExplicitSearchCriteria("map", map);

            long[] bounds = getMapBounds(mapFilter, mapScale);
            for (long x = bounds[0]; x < bounds[1]; x = x + PIXELS_PER_CALL) {
                for (long y = bounds[2]; y < bounds[3]; y = y + PIXELS_PER_CALL) {
                    SearchQuery searchQuery = new SearchQuery(0);
                    searchQuery.setFilters(List.of(
                            mapFilter,
                            new LongRangeSearchCriteria("entity.position.x", mapScale.toGameScaleX(x),mapScale.toGameScaleX(x + PIXELS_PER_CALL) -1, false),
                            new LongRangeSearchCriteria("entity.position.y",mapScale.toGameScaleY(y),mapScale.toGameScaleY(y + PIXELS_PER_CALL) -1, false)));
                    searchQuery.setAggregations(List.of(COUNT_AGGREGATION));

                    SearchResult searchResult = searchRepository.fetch("tfms-match-event",searchQuery);
                    long count = getCountFromResult(searchResult, "count");
                    if (count != 0) {
                        LOGGER.info("{}|{}",x,y);
                        ObjectNode entry = JsonUtil.emptyObjectNode();
                        entry.put("x", mapScale.toMinimapFormatX(mapScale.toGameScaleX(x)));
                        entry.put("y", mapScale.toMinimapFormatY(mapScale.toGameScaleY(y)));
                        entry.put("count", count);
                        resultArray.add(entry);
                        if (highestCount < count) {
                            highestCount = count;
                        }
                    }
                }
            }
            result.put("highest", highestCount);
            heatmaps.put(map, result);
            return result;

        }
        throw new InternalJsonException("A");
    }

    protected long[] getMapBounds(SearchCriteria mapName, MapScale mapScale) {
        long[] bounds = new long[4];
        SearchQuery searchQuery = new SearchQuery(0);
        searchQuery.setFilters(List.of(mapName));
        searchQuery.setAggregations(List.of(
                        new SimpleFieldAggregation("xMin","entity.position.x", SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("xMax","entity.position.x", SearchAggregation.AggregationType.MAX),
                        new SimpleFieldAggregation("yMin","entity.position.y", SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("yMax","entity.position.y", SearchAggregation.AggregationType.MAX)));

        SearchResult searchResult = searchRepository.fetch("tfms-match-event",searchQuery);
        bounds[0] = mapScale.toMinimapScaleX(getCountFromResult(searchResult,"xMin"));
        bounds[1] = mapScale.toMinimapScaleX(getCountFromResult(searchResult,"xMax"));
        bounds[2] = mapScale.toMinimapScaleY(getCountFromResult(searchResult,"yMin"));
        bounds[3] = mapScale.toMinimapScaleY(getCountFromResult(searchResult,"yMax"));

        return bounds;
    }

    protected long getCountFromResult(SearchResult searchResult, String name) {
        return Math.round((double) ((SimpleAggregationResult) searchResult.getAggregations().get(name)).getValue());
    }
}
