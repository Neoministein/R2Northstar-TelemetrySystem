package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.persistence.HeatmapType;
import com.neo.util.framework.api.queue.OutgoingQueueConnection;
import com.neo.util.framework.api.queue.QueueMessage;
import com.neo.util.framework.api.queue.QueueService;
import com.neo.util.framework.api.request.RequestDetails;
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

    public void addMatchToQueue(String matchId) {
        QueueableHeatmapInstruction queueableHeatmapInstruction = new QueueableHeatmapInstruction();
        queueableHeatmapInstruction.setMatchId(matchId);
        queueableHeatmapInstruction.setType(HeatmapType.PLAYER_POSITION);
        queueService.addToQueue(QUEUE_NAME,(new QueueMessage(
                requestDetails, QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE, queueableHeatmapInstruction)));
    }

}
