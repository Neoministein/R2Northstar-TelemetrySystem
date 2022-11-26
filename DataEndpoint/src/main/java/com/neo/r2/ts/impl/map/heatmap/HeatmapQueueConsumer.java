package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.HeatmapType;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.r2.ts.impl.persistence.repository.HeatmapRepository;
import com.neo.r2.ts.impl.persistence.repository.MatchRepository;
import com.neo.util.common.impl.exception.ConfigurationException;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.framework.api.queue.IncomingQueueConnection;
import com.neo.util.framework.api.queue.QueueListener;
import com.neo.util.framework.api.queue.QueueMessage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
@IncomingQueueConnection(HeatmapQueueService.QUEUE_NAME)
public class HeatmapQueueConsumer implements QueueListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeatmapQueueConsumer.class);

    @Inject
    protected MapService mapService;

    @Inject
    protected HeatmapFactory heatmapFactory;

    @Inject
    protected HeatmapRepository heatmapRepository;

    @Inject
    protected MatchRepository matchRepository;

    @Override
    @Transactional
    public void onMessage(QueueMessage queueMessage) {
        try {
            if (QueueableHeatmapInstruction.QUEUE_MESSAGE_TYPE.equals(queueMessage.getMessageType())) {
                QueueableHeatmapInstruction heatmapInstruction = (QueueableHeatmapInstruction) queueMessage.getPayload();
                if (HeatmapType.FULL_MAP_AGGREGATION.equals(heatmapInstruction.getType())) {

                    Optional<GameMap> gameMap = mapService.getMap(heatmapInstruction.getMap());
                    gameMap.ifPresent(map -> heatmapRepository.create(heatmapFactory.createForMap(map)));

                } else {
                    createHeatmapForMatch(heatmapInstruction.getMatchId(), heatmapInstruction.getType());
                }
            }
        } catch (ConfigurationException | ValidationException ex) {
            LOGGER.error("An error occurred while generation the heatmap");
        }
    }

    protected void createHeatmapForMatch(String matchId, HeatmapType type) {
        Optional<Match> match = matchRepository.getMatchById(matchId);
        if (match.isEmpty()) {
            return;
        }

        Heatmap heatmap = heatmapFactory.createForMatch(matchId, mapService.getMap(match.get().getMap()).get(), type);
        heatmap.setMatch(match.get());
        matchRepository.edit(match.get());
    }
}
