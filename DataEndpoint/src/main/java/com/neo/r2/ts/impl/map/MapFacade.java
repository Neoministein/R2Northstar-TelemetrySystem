package com.neo.r2.ts.impl.map;

import com.neo.r2.ts.impl.map.heatmap.HeatmapFactory;
import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapScale;
import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.match.MatchService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.r2.ts.impl.rest.CustomConstants;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class MapFacade {

    @Inject
    protected MapScalingService mapScalingService;

    @Inject
    protected MatchService matchService;

    @Inject
    protected HeatmapFactory heatmapFactory;

    @Inject
    protected EntityRepository entityRepository;

    public Heatmap getHeatmapOfMap(String map) {
        getMap(map); //Throws exception if map isn't valid

        EntityQuery<Heatmap> heatmapEntityQuery = new EntityQuery<>(
                Heatmap.class,
                0,
                1,
                List.of(new ExplicitSearchCriteria(Heatmap.C_MAP, map),
                        new ExplicitSearchCriteria(Heatmap.C_TYPE, HeatmapType.FULL_MAP_AGGREGATION)),
                Map.of(PersistenceEntity.C_ID, false));

        return entityRepository.find(heatmapEntityQuery).getFirst()
                .orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_NO_HEATMAP_FOR_MAP, map));
    }

    @Transactional
    public Heatmap generateHeatmapForMap(String map) {
        Heatmap heatmap = heatmapFactory.createForMap(getMap(map));
        entityRepository.create(heatmap);
        return heatmap;
    }

    @Transactional
    public Heatmap generateHeatmapForMatch(String matchId, HeatmapType heatmapType) {
        Optional<Match> match = matchService.getMatch(matchId);
        if (match.isEmpty()) {
            return null;
        }

        Heatmap heatmap = heatmapFactory.createForMatch(matchId, getMap(match.get().getMap()), heatmapType);
        matchService.addHeatmap(match.get(), heatmap);
        return heatmap;
    }

    public GameMap getMap(String map) {
        return mapScalingService.getMap(map).orElseThrow(() -> new NoContentFoundException(
                CustomConstants.EX_UNSUPPORTED_MAP, map));
    }

    public MapScale getMapScale(String map) {
        return mapScalingService.getMapScale(map).orElseThrow(() -> new NoContentFoundException(
                CustomConstants.EX_UNSUPPORTED_MAP, map));
    }

    public List<GameMap> getMaps() {
        return mapScalingService.getMaps();
    }
}
