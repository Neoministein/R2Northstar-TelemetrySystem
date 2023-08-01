package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.repository.entity.HeatmapRepository;
import com.neo.r2.ts.persistence.HeatmapEnums;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.util.framework.api.scheduler.CronSchedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class HeatmapCreationScheduler {

    @Inject
    protected MapService mapService;

    @Inject
    protected HeatmapFactory heatmapFactory;

    @Inject
    protected HeatmapRepository heatmapRepository;

    @Inject
    protected HeatmapQueueService heatmapQueueService;

    @Transactional
    @CronSchedule(value = "HeatmapCreationScheduler", cron = "0 1 * * *")
    public void action() {
        for (GameMap gameMap: mapService.fetchAll()) {
            Heatmap heatmap = heatmapFactory.createForMap(gameMap.name(), HeatmapEnums.Type.PLAYER_POSITION);
            heatmapRepository.create(heatmap);
            heatmapQueueService.addHeatmapToGenerationQueue(heatmap.getId());
        }
    }
}
