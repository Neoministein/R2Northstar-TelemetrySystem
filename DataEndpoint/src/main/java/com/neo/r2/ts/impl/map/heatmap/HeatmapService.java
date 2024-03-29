package com.neo.r2.ts.impl.map.heatmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.map.scaling.MapScale;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.match.event.processor.player.movement.PlayerPositionEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.repository.entity.HeatmapRepository;
import com.neo.r2.ts.impl.repository.searchable.MatchEventRepository;
import com.neo.r2.ts.persistence.HeatmapEnums;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.util.common.impl.exception.ConfigurationException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.aggregation.CriteriaAggregation;
import com.neo.util.framework.api.persistence.aggregation.CriteriaAggregationResult;
import com.neo.util.framework.api.persistence.aggregation.SimpleAggregationResult;
import com.neo.util.framework.api.persistence.aggregation.SimpleFieldAggregation;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.LongRangeSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.SearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class HeatmapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapService.class);

    protected static final String COUNT = "count";
    protected static final String PLAYER_POS_X = "entity.position.x";
    protected static final String PLAYER_POS_Y = "entity.position.y";

    protected static final SimpleFieldAggregation COUNT_AGGREGATION = new SimpleFieldAggregation(COUNT, MatchStateWrapper.MATCH_ID, SimpleFieldAggregation.Type.COUNT);


    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected MapService mapService;

    @Inject
    protected MatchEventRepository matchEventRepository;
    @Inject
    protected HeatmapRepository heatmapRepository;

    public Heatmap calculateHeatmap(long heatmapId) {
        return calculateHeatmap(heatmapRepository.fetch(heatmapId).orElseThrow());
    }

    public Heatmap calculateHeatmap(Heatmap heatmap) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();

        searchCriteriaList.add(new ExplicitSearchCriteria(MatchStateWrapper.MAP, heatmap.getMap()));

        if (heatmap.getMatch() != null) {
            searchCriteriaList.add(new ExplicitSearchCriteria(MatchStateWrapper.MATCH_ID, heatmap.getMatch().getId().toString()));
        }

        if (HeatmapEnums.Type.valueOf(heatmap.getType()) == HeatmapEnums.Type.PLAYER_POSITION) {
            searchCriteriaList.add(new ExplicitSearchCriteria("eventType", PlayerPositionEventProcessor.EVENT_NAME));
        }

        try {
            generateHeatmap(heatmap, searchCriteriaList);
        } catch (Exception ex) {
            LOGGER.warn("Failed to calculate Heatmap: [{}] ErrorMessage: [{}]", heatmap.getId(), ex.getMessage());
            heatmap.setStatus(HeatmapEnums.ProcessState.FAILED.toString());
            heatmap.setData(null);
        }

        return heatmap;
    }

    protected void generateHeatmap(Heatmap heatmap, List<SearchCriteria> basicCriteria) {
        if (!searchProvider.enabled()) {
            throw new ConfigurationException(CustomConstants.EX_SERVICE_UNAVAILABLE);
        }

        MapScale mapScale = mapService.requestMap(heatmap.getMap()).scale();

        ObjectNode result =  JsonUtil.emptyObjectNode();
        ArrayNode resultArray = JsonUtil.emptyArrayNode();
        result.set("entries", resultArray);

        Bounds bounds = getMapBounds(basicCriteria, mapScale);
        for (long x = bounds.xMin; x < bounds.xMax; x = x + heatmap.getPixelDensity()) {
            Map<String, SearchCriteria> criteriaMap = new HashMap<>();
            for (long y = bounds.yMin; y < bounds.yMax; y = y + heatmap.getPixelDensity()) {
                criteriaMap.put(Long.toString(y), new LongRangeSearchCriteria(PLAYER_POS_Y,mapScale.toGameScaleY(y),mapScale.toGameScaleY(y + heatmap.getPixelDensity()) -1, false));
            }
            SearchQuery searchQuery = new SearchQuery(0);
            List<SearchCriteria> searchCriteriaList = new ArrayList<>(basicCriteria);
            searchCriteriaList.add(new LongRangeSearchCriteria(PLAYER_POS_X, mapScale.toGameScaleX(x),mapScale.toGameScaleX(x + heatmap.getPixelDensity()) -1, false));
            searchQuery.setFilters(searchCriteriaList);
            searchQuery.setAggregations(List.of(new CriteriaAggregation("values", criteriaMap, COUNT_AGGREGATION)));
            SearchResult<JsonNode> searchResult = searchProvider.fetch(matchEventRepository.getIndexName(),searchQuery);
            for (Map.Entry<String, Object> entry: ((CriteriaAggregationResult) searchResult.getAggregations().get("values")).getCriteriaResult().entrySet()) {
                long count = parseLongFromDouble(entry.getValue());
                if (count != 0) {
                    ObjectNode node = JsonUtil.emptyObjectNode();
                    node.put("x", mapScale.toMinimapFormatX(mapScale.toGameScaleX(x)));
                    node.put("y", mapScale.toMinimapFormatY(mapScale.toGameScaleY(Long.parseLong(entry.getKey()))));
                    node.put(COUNT, count);
                    resultArray.add(node);
                    if (heatmap.getHighestCount() < count) {
                        heatmap.setHighestCount(count);
                    }
                }
            }
        }
        heatmap.setData(result);
        heatmap.setStatus(HeatmapEnums.ProcessState.FINISHED.toString());
    }

    protected Bounds getMapBounds(List<SearchCriteria> searchCriteriaList, MapScale mapScale) {
        /*
        SearchQuery searchQuery = new SearchQuery(0);
        searchQuery.setFilters(searchCriteriaList);
        searchQuery.setAggregations(List.of(
                        new SimpleFieldAggregation("xMin",PLAYER_POS_X, SimpleFieldAggregation.Type.MIN),
                        new SimpleFieldAggregation("xMax",PLAYER_POS_X, SimpleFieldAggregation.Type.MAX),
                        new SimpleFieldAggregation("yMin",PLAYER_POS_Y, SimpleFieldAggregation.Type.MIN),
                        new SimpleFieldAggregation("yMax",PLAYER_POS_Y, SimpleFieldAggregation.Type.MAX)));

        SearchResult searchResult = searchRepository.fetch("r2ts-match-event",searchQuery);
        Bounds bounds = new Bounds(
                mapScale.toMinimapScaleX(getCountFromResult(searchResult,"xMin")),
                mapScale.toMinimapScaleX(getCountFromResult(searchResult,"xMax")),
                mapScale.toMinimapScaleY(getCountFromResult(searchResult,"yMin")),
                mapScale.toMinimapScaleY(getCountFromResult(searchResult,"yMax"))
        );
         */
        Bounds bounds = new Bounds(0,1024,0,1024);
        LOGGER.debug("Bounds for current heatmap calculations [{}]", bounds);
        return bounds;
        //FIXME Currently disabled due to the xMin being higher than it should be
    }

    protected long getCountFromResult(SearchResult searchResult, String name) {
        return parseLongFromDouble(((SimpleAggregationResult) searchResult.getAggregations().get(name)).getValue());
    }

    protected long parseLongFromDouble(Object value) {
        return Math.round((double) value);
    }

    protected record Bounds(long xMin, long xMax, long yMin, long yMax) {
        @Override
        public String toString() {
            return JsonUtil.toJson(this);
        }
    }
}
