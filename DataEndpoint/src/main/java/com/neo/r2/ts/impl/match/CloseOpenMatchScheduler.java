package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.repository.entity.MatchRepository;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.framework.api.config.Config;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.scheduler.FixedRateSchedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CloseOpenMatchScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseOpenMatchScheduler.class);

    protected final MatchService matchService;
    protected final GlobalMatchState globalMatchState;
    protected final MatchRepository matchRepository;

    protected final ConfigService configService;

    @Inject
    public CloseOpenMatchScheduler(MatchService matchService, GlobalMatchState globalMatchState,
                                   MatchRepository matchRepository, ConfigService configService) {
        this.matchService = matchService;
        this.globalMatchState = globalMatchState;
        this.matchRepository = matchRepository;
        this.configService = configService;
    }

    @FixedRateSchedule(value = "CloseOpenMatchScheduler", delay = 1, timeUnit = TimeUnit.MINUTES)
    public void action() {
        Instant cutOfDate = getCutOfDate();
        LOGGER.info("Closing open matches with cut of date [{}].", cutOfDate);

        for (Match match: matchRepository.fetchArePlaying(cutOfDate)) {
            Optional<MatchStateWrapper> matchStateOptional = globalMatchState.getCurrentMatchState(match.getId().toString());
            if (matchStateOptional.isEmpty() || matchStateOptional.get().getLastUpdated().isBefore(cutOfDate)) {
                LOGGER.info("Force closing match [{}]", match.getStringId());
                matchService.endMatch(match.getStringId());
            }
        }
    }

    protected Instant getCutOfDate() {
        Config config = configService.get("r2ts.closeOpenMatch");

        Long delay = config.get("delay").asLong().orElse(2L);
        TimeUnit timeUnit = config.get("time-unit").asString().map(TimeUnit::valueOf).orElse(TimeUnit.MINUTES);

        return Instant.now().minus(Duration.ofSeconds(timeUnit.toSeconds(delay)));
    }
}
