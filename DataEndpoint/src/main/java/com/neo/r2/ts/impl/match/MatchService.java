package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.map.heatmap.HeatmapQueueService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.r2.ts.impl.repository.MatchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);

    @Inject
    protected MatchRepository matchRepository;

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected HeatmapQueueService heatmapQueueService;

    public Match endMatch(Match match) {
        match.setIsRunning(false);

        matchRepository.edit(match);
        matchStateService.matchEnded(match.getId());

        LOGGER.info("Match finished {}", match.getId());
        heatmapQueueService.addMatchToQueue(match.getId().toString());
        LOGGER.info("Add match {} to queue for heatmap generation", match.getId());
        return match;
    }
}
