package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.framework.api.scheduler.FixedRateSchedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CloseOpenMatchScheduler {

    @Inject
    protected MatchService matchService;

    @Inject
    protected GlobalMatchState globalMatchState;

    @Inject
    protected MatchRepository matchRepository;

    @FixedRateSchedule(value = "CloseOpenMatchScheduler", delay = 1, timeUnit = TimeUnit.MINUTES)
    public void action() {
        Instant cutOfDate = Instant.now().minus(Duration.ofMinutes(2));

        for (Match match: matchRepository.fetchArePlaying(cutOfDate)) {
            Optional<MatchStateWrapper> matchStateOptional = globalMatchState.getCurrentMatchState(match.getId().toString());
            if (matchStateOptional.isEmpty()) {
                matchService.endMatch(match);
                break;
            }

            MatchStateWrapper matchStateWrapper = matchStateOptional.get();
            if (matchStateWrapper.getTimeStamp().isBefore(cutOfDate)) {
                matchService.endMatch(match);
            }
        }
    }
}
