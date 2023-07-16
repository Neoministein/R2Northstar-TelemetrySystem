package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.framework.api.queue.OutgoingQueueConnection;
import com.neo.util.framework.api.queue.QueueService;
import com.neo.util.framework.api.request.RequestDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

@ApplicationScoped
@OutgoingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueService {

    public static final String QUEUE_NAME = "heatmapGeneration";

    @Inject
    protected QueueService queueService;

    @Inject
    protected Provider<RequestDetails> requestDetails;

    public void addHeatmapToGenerationQueue(long heatmapId) {
        queueService.addToQueue(QUEUE_NAME, heatmapId);
    }

}
