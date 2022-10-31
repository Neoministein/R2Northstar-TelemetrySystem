package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.map.scaling.MapScalingService;
import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.r2.ts.impl.rest.CustomConstants;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class MatchFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchFacade.class);

    @Inject
    protected MapScalingService mapScalingService;

    @Inject
    protected MatchService matchService;

    @Inject
    protected EntityRepository entityRepository;


    @Transactional
    public Match createNewMatch(String nsServerName, String map, String gamemode, int maxPlayers) {
        if (mapScalingService.getMap(map).isEmpty()) {
            throw new ValidationException(CustomConstants.EX_UNSUPPORTED_MAP, map);
        }

        Match match = new Match();
        match.setMap(map);
        match.setNsServerName(nsServerName);
        match.setGamemode(gamemode);
        match.setMaxPlayers(maxPlayers);

        entityRepository.create(match);
        LOGGER.info("New match registered {}", match.getId());
        return match;
    }

    @Transactional
    public Match getMatch(String matchId) {
        return matchService.getMatch(matchId).orElseThrow(() ->
                new NoContentFoundException(CustomConstants.EX_MATCH_NON_EXISTENT, matchId));
    }

    @Transactional
    public Match endMatch(String matchId) {
        Match match = getMatch(matchId);

        if (!match.getIsRunning()) {
            throw new ValidationException(CustomConstants.EX_ALREADY_MATCH_ENDED, matchId);
        }

        return matchService.endMatch(match);
    }

    @Transactional
    public Heatmap getHeatmapOfMatch(String matchId) {
        List<Heatmap> heatmaps = getMatch(matchId).getHeatmaps();
        if (heatmaps.isEmpty()) {
            throw new NoContentFoundException(CustomConstants.EX_NO_HEATMAP_FOR_MATCH, matchId);
        }
        return getMatch(matchId).getHeatmaps().get(0);
    }
}
