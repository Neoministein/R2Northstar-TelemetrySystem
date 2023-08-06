package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.framework.api.queue.OutgoingQueue;
import com.neo.util.framework.api.queue.QueueService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@OutgoingQueue(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueService {

    public static final String QUEUE_NAME = "heatmapGeneration";

    @Inject
    protected QueueService queueService;

    public void addHeatmapToGenerationQueue(long heatmapId) {
        queueService.addToQueue(QUEUE_NAME, heatmapId);
    }

}
