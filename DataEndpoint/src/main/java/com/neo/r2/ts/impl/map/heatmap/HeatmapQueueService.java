package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.util.framework.api.connection.RequestDetails;
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

    @Inject
    protected RequestDetails requestDetails;

    public void addToQueue(QueueMessage queueMessage) {
        queueService.addToQueue(QUEUE_NAME, queueMessage);
    }

    public void addMatchToQueue(String matchId) {
        QueueableHeatmapInstruction queueableHeatmapInstruction = new QueueableHeatmapInstruction();
        queueableHeatmapInstruction.setMatchId(matchId);
        queueableHeatmapInstruction.setType(HeatmapType.PLAYER_POSITION);
        addToQueue(new QueueMessage(
                requestDetails, QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE, queueableHeatmapInstruction));
    }

}
