package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.util.framework.api.queue.IncomingQueueConnection;
import com.neo.util.framework.api.queue.QueueListener;
import com.neo.util.framework.api.queue.QueueMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@IncomingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueConsumer implements QueueListener {

    @Inject
    protected HeatmapGeneratorImpl heatmapGenerator;

    @Override
    public void onMessage(QueueMessage queueMessage) {
        if (QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE.equals(queueMessage.getMessageType())) {
            QueueableHeatmapInstruction heatmapInstruction = (QueueableHeatmapInstruction) queueMessage.getPayload();
            if (HeatmapType.FULL_MAP_AGGREGATION.equals(heatmapInstruction.getType())) {
                heatmapGenerator.calculateMap(heatmapInstruction.getMap());
            } else {
                heatmapGenerator.calculateMatch(heatmapInstruction.getMatchId(), heatmapInstruction.getType());
            }
        }
    }
}
