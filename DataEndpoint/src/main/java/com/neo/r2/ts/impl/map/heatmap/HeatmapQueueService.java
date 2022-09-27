package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.framework.api.queue.OutgoingQueueConnection;
import com.neo.util.framework.api.queue.QueueMessage;
import com.neo.util.framework.api.queue.QueueService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@OutgoingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueService {

    public static final String QUEUE_NAME = "heatmapGeneration";

    @Inject
    protected QueueService queueService;

    public void addToQueue(QueueMessage queueMessage) {
        queueService.addToQueue(QUEUE_NAME, queueMessage);
    }

}
