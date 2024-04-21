package com.neo.r2.ts.impl.match.state;

import com.neo.r2.ts.impl.match.MatchStatusEvent;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.entity.Match;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class GlobalMatchState {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalMatchState.class);

    protected final MatchRepository matchRepository;
    protected final Map<String, MatchStateWrapper> matchStateMap;

    @Inject
    public GlobalMatchState(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        this.matchStateMap = new ConcurrentHashMap<>();
    }

    public void handleMatchStatusEvent(@Observes MatchStatusEvent matchStatusEvent) {
        if (MatchStatusEvent.Type.CREATED.equals(matchStatusEvent.type())) {
            LOGGER.info("Setting up global match-state for match [{}]", matchStatusEvent.matchId());
            Match match = matchRepository.requestById(matchStatusEvent.matchId());
            matchStateMap.put(matchStatusEvent.matchId(), new MatchStateWrapper(match));
        } else if (MatchStatusEvent.Type.ENDED.equals(matchStatusEvent.type())) {
            LOGGER.info("Cleaning up global match-state for match [{}]", matchStatusEvent.matchId());
            matchStateMap.remove(matchStatusEvent.matchId());
        }
    }

    public Optional<MatchStateWrapper> getCurrentMatchState(String matchId) {
        return Optional.ofNullable(matchStateMap.get(matchId));
    }

}
