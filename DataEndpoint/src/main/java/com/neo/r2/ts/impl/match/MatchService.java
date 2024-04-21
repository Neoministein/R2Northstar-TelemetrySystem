package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.r2.ts.web.rest.dto.inbound.NewMatchDto;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.common.impl.exception.ValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Transactional
@ApplicationScoped
public class MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);

    protected final MapService mapService;

    protected final MatchRepository matchRepository;
    protected final Event<MatchStatusEvent> matchStatusEvent;

    @Inject
    public MatchService(MatchRepository matchRepository, MapService mapService, Event<MatchStatusEvent> matchStatusEvent) {
        this.matchRepository = matchRepository;
        this.mapService = mapService;
        this.matchStatusEvent = matchStatusEvent;
    }

    public Match createMatch(NewMatchDto newMatchDto, ApplicationUser creator) {
        List<String> tags = List.of();
        if(!StringUtils.isEmpty(newMatchDto.tags())) {
            tags = List.of(newMatchDto.tags().replaceAll("\\s+", "").split(","));
        }

        Match match = new Match(newMatchDto.nsServerName(),
                mapService.requestMap(newMatchDto.map()).name(),
                newMatchDto.gamemode(),
                newMatchDto.maxPlayers(),
                newMatchDto.milliSecBetweenState(),
                creator,
                tags);

        matchRepository.create(match); //Manually persists to get an exception before initializing the match-state
        LOGGER.info("New match registered [{}]", match.getId());
        matchStatusEvent.fire(new MatchStatusEvent(MatchStatusEvent.Type.CREATED, match.getStringId()));
        return match;
    }

    public Match endMatch(String matchId) {
        Match match = matchRepository.requestById(matchId);

        if (!match.getIsRunning()) {
            throw new ValidationException(CustomConstants.EX_ALREADY_MATCH_ENDED, matchId);
        }

        match.setIsRunning(false);
        match.setEndDate(Instant.now());

        LOGGER.info("Match finished {}", match.getId());
        matchStatusEvent.fire(new MatchStatusEvent(MatchStatusEvent.Type.ENDED, matchId));
        return match;
    }
}
