package com.neo.r2.ts.impl.map.heatmap;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScale;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.r2.ts.impl.persistence.searchable.MatchEventSearchable;
import com.neo.util.common.impl.exception.InternalJsonException;
import com.neo.util.common.impl.exception.InternalLogicException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.aggregation.SearchAggregation;
import com.neo.util.framework.api.persistence.aggregation.SimpleAggregationResult;
import com.neo.util.framework.api.persistence.aggregation.SimpleFieldAggregation;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.LongRangeSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.SearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchRepository;
import com.neo.util.framework.api.persistence.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import java.util.*;

@ApplicationScoped
public class HeatmapGeneratorImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapGeneratorImpl.class);

    protected static final SearchAggregation COUNT_AGGREGATION = new SimpleFieldAggregation("count", MatchEventSearchable.F_MATCH_ID, SearchAggregation.AggregationType.COUNT);

    protected static final String PLAYER_POS_X = "entity.position.x";
    protected static final String PLAYER_POS_Y = "entity.position.y";

    protected static final int PIXELS_PER_CALL = 4;

    @Inject
    EntityRepository repository;

    @Inject
    SearchRepository searchRepository;

    @Inject
    MapScalingService mapScalingService;

    public Heatmap calculateMap(String map) {
        Heatmap heatmap = calculate(mapScalingService.getMap(map), List.of(new ExplicitSearchCriteria("map", map)), PIXELS_PER_CALL);
        heatmap.setType(HeatmapType.FULL_MAP_AGGREGATION);
        heatmap.setDescription("This is an autogenerated heatmap for the map " + map);
        try {
            repository.create(heatmap);
        } catch (RollbackException ex) {
            throw new InternalLogicException("Cannot save rollback exception", ex);
        }

        return heatmap;
    }

    public Heatmap calculateMatch(String matchId, HeatmapType heatmapType) {
        Optional<Match> match = repository.find(UUID.fromString(matchId), Match.class);
        if (match.isPresent()) {
            List<SearchCriteria> searchCriteriaList = new ArrayList<>();
            switch (heatmapType) {
                case PLAYER_POSITION:
                    searchCriteriaList.add(new ExplicitSearchCriteria(
                            MatchEventSearchable.F_MATCH_ID, match.get().getId().toString()));
                    break;
                case FULL_MAP_AGGREGATION:
                case CUSTOM:
                default:
                    throw new InternalLogicException("Unsupported heatmap type");
            }

            Heatmap heatmap = calculate(mapScalingService.getMap(match.get().getMap()),searchCriteriaList, PIXELS_PER_CALL);
            heatmap.setType(heatmapType);
            heatmap.setDescription("This is an autogenerated heatmap for the match " + matchId);
            try {
                repository.create(heatmap);
            } catch (RollbackException ex) {
                throw new InternalLogicException("Cannot save rollback exception", ex);
            }

            return heatmap;
        }
        throw new InternalLogicException("Unknown match id");
    }

    public Heatmap calculate(GameMap gameMap, List<SearchCriteria> searchCriteria, int resolution) {
        if (searchRepository.enabled()) {
            Heatmap heatmap = new Heatmap();
            heatmap.setType(HeatmapType.CUSTOM);
            heatmap.setHighestCount(0L);
            heatmap.setMap(gameMap.getName());

            MapScale mapScale = gameMap.getMapScale();
            ObjectNode result =  JsonUtil.emptyObjectNode();
            ArrayNode resultArray = JsonUtil.emptyArrayNode();
            result.set("entries", resultArray);

            long[] bounds = getMapBounds(searchCriteria, mapScale);
            for (long x = bounds[0]; x < bounds[1]; x = x + resolution) {
                for (long y = bounds[2]; y < bounds[3]; y = y + resolution) {
                    SearchQuery searchQuery = new SearchQuery(0);
                    List<SearchCriteria> entryCriteria = new ArrayList<>(searchCriteria);
                    entryCriteria.add(new LongRangeSearchCriteria(PLAYER_POS_X, mapScale.toGameScaleX(x),mapScale.toGameScaleX(x + PIXELS_PER_CALL) -1, false));
                    entryCriteria.add(new LongRangeSearchCriteria(PLAYER_POS_Y,mapScale.toGameScaleY(y),mapScale.toGameScaleY(y + PIXELS_PER_CALL) -1, false));
                    searchQuery.setFilters(entryCriteria);
                    searchQuery.setAggregations(List.of(COUNT_AGGREGATION));

                    SearchResult searchResult = searchRepository.fetch("r2ts-match-event",searchQuery);
                    long count = getCountFromResult(searchResult, "count");
                    if (count != 0) {
                        LOGGER.info("{}|{}",x,y);
                        ObjectNode entry = JsonUtil.emptyObjectNode();
                        entry.put("x", mapScale.toMinimapFormatX(mapScale.toGameScaleX(x)));
                        entry.put("y", mapScale.toMinimapFormatY(mapScale.toGameScaleY(y)));
                        entry.put("count", count);
                        resultArray.add(entry);
                        if (heatmap.getHighestCount() < count) {
                            heatmap.setHighestCount(count);
                        }
                    }
                }
            }
            heatmap.setData(result);
            return heatmap;

        }
        throw new InternalJsonException("A");
    }

    protected long[] getMapBounds(List<SearchCriteria> searchCriteriaList, MapScale mapScale) {
        long[] bounds = new long[4];
        SearchQuery searchQuery = new SearchQuery(0);
        searchQuery.setFilters(searchCriteriaList);
        searchQuery.setAggregations(List.of(
                        new SimpleFieldAggregation("xMin",PLAYER_POS_X, SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("xMax",PLAYER_POS_X, SearchAggregation.AggregationType.MAX),
                        new SimpleFieldAggregation("yMin",PLAYER_POS_Y, SearchAggregation.AggregationType.MIN),
                        new SimpleFieldAggregation("yMax",PLAYER_POS_Y, SearchAggregation.AggregationType.MAX)));

        SearchResult searchResult = searchRepository.fetch("r2ts-match-event",searchQuery);
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
