package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.map.heatmap.HeatmapFactory;
import com.neo.r2.ts.impl.map.heatmap.HeatmapQueueService;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.repository.entity.HeatmapRepository;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.HeatmapEnums;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.r2.ts.web.rest.dto.inbound.NewMatchDto;
import com.neo.util.common.impl.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);

    @Inject
    protected MapService mapService;

    @Inject
    protected MatchRepository matchRepository;

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected HeatmapQueueService heatmapQueueService;

    @Inject
    protected HeatmapRepository heatmapRepository;

    @Inject
    protected HeatmapFactory heatmapFactory;

    public Match createMatch(NewMatchDto newMatchDto, ApplicationUser creator) {
        Match match = new Match();
        match.setMap(mapService.requestMap(newMatchDto.map()).name());
        match.setNsServerName(newMatchDto.nsServerName());
        match.setGamemode(newMatchDto.gamemode());
        match.setMaxPlayers(newMatchDto.maxPlayers());
        match.setUser(creator);
        if(!StringUtils.isEmpty(newMatchDto.tags())) {
            match.setTags(List.of(newMatchDto.tags().replaceAll("\\s+", "").split(",")));
        }
        match.setMilliSecBetweenState(newMatchDto.milliSecBetweenState());

        matchRepository.create(match); //Manually persists to get an exception before initializing the match-state
        LOGGER.info("New match registered [{}]", match.getId());
        matchStateService.initializeMatchState(match);
        return match;
    }

    public Match endMatch(Match match) {
        match.setIsRunning(false);

        matchRepository.edit(match);
        LOGGER.info("Match finished {}", match.getId());

        Heatmap heatmap = heatmapFactory.createForMatch(match, HeatmapEnums.Type.PLAYER_POSITION);
        heatmapRepository.create(heatmap);
        heatmapQueueService.addHeatmapToGenerationQueue(heatmap.getId());
        LOGGER.info("Add match {} to queue for heatmap generation", match.getId());

        matchStateService.matchEnded(match.getId().toString());
        return match;
    }
}
