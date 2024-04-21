package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.framework.api.queue.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IncomingQueue(HeatmapQueueService.QUEUE_NAME)
@OutgoingQueue(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueService implements QueueListener {

    public static final String QUEUE_NAME = "heatmapGeneration";

    protected final QueueService queueService;
    protected final HeatmapService heatmapService;

    @Inject
    public HeatmapQueueService(QueueService queueService, HeatmapService heatmapService) {
        this.queueService = queueService;
        this.heatmapService = heatmapService;
    }

    public void addHeatmapToGenerationQueue(long heatmapId) {
        queueService.addToQueue(QUEUE_NAME, heatmapId);
    }

    @Override
    @Transactional
    public void onMessage(QueueMessage queueMessage) {
        heatmapService.calculateHeatmap((long) queueMessage.getPayload());
    }
}
