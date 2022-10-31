package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.map.heatmap.HeatmapQueueService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);

    protected static final EntityQuery<Match> Q_ARE_PLAYING = new EntityQuery<>(Match.class, 0,null,
            List.of(new ExplicitSearchCriteria(Match.C_IS_PLAYING,true)),
            Map.of(Match.C_START_DATE, false));

    protected static final EntityQuery<Match> Q_STOPPLED_PLAYING = new EntityQuery<>(Match.class, 0,null,
            List.of(new ExplicitSearchCriteria(Match.C_IS_PLAYING,false)),
            Map.of(Match.C_START_DATE, false));

    @Inject
    protected EntityRepository entityRepository;

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected HeatmapQueueService heatmapQueueService;

    public Optional<Match> getMatch(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        try {
            return entityRepository.find(UUID.fromString(id), Match.class);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public Match endMatch(Match match) {
        match.setIsRunning(false);

        entityRepository.edit(match);
        matchStateService.matchEnded(match.getId());

        LOGGER.info("Match finished {}", match.getId());
        heatmapQueueService.addMatchToQueue(match.getId().toString());
        LOGGER.info("Add match {} to queue for heatmap generation", match.getId());
        return match;
    }

    public List<Match> getArePlaying() {
        return entityRepository.find(Q_ARE_PLAYING).getHits();
    }

    public List<Match> getStoppedPlaying() {
        return entityRepository.find(Q_STOPPLED_PLAYING).getHits();
    }

    public void addHeatmap(Match match, Heatmap heatmap) {
        match.getHeatmaps().add(heatmap);
        heatmap.setMatch(match);
        entityRepository.edit(match);
    }
}
