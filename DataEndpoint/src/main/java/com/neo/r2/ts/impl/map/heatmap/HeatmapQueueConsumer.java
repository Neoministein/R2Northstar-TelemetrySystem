package com.neo.r2.ts.impl.map.heatmap;

import com.neo.util.framework.api.queue.IncomingQueueConnection;
import com.neo.util.framework.api.queue.QueueListener;
import com.neo.util.framework.api.queue.QueueMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IncomingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueConsumer implements QueueListener {

    @Inject
    protected HeatmapService heatmapService;

    @Override
    @Transactional
    public void onMessage(QueueMessage queueMessage) {
        heatmapService.calculateHeatmap((long) queueMessage.getPayload());
    }
}
