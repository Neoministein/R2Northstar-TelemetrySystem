package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.map.MapFacade;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.util.common.impl.exception.ConfigurationException;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.framework.api.queue.IncomingQueueConnection;
import com.neo.util.framework.api.queue.QueueListener;
import com.neo.util.framework.api.queue.QueueMessage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@IncomingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueConsumer implements QueueListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapQueueConsumer.class);

    @Inject
    protected MapFacade mapFacade;

    @Override
    public void onMessage(QueueMessage queueMessage) {
        try {
            if (QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE.equals(queueMessage.getMessageType())) {
                QueueableHeatmapInstruction heatmapInstruction = (QueueableHeatmapInstruction) queueMessage.getPayload();
                if (HeatmapType.FULL_MAP_AGGREGATION.equals(heatmapInstruction.getType())) {
                    mapFacade.generateHeatmapForMap(heatmapInstruction.getMap());
                } else {
                    mapFacade.generateHeatmapForMatch(heatmapInstruction.getMatchId(), heatmapInstruction.getType());
                }
            }
        } catch (ConfigurationException | ValidationException ex) {
            LOGGER.error("An error occurred while generation the heatmap");
        }

    }
}
