package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.api.scheduler.AbstractScheduler;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.framework.api.persistence.criteria.DateSearchCriteria;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CloseOpenMatchScheduler extends AbstractScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseOpenMatchScheduler.class);

    protected static final int TWO_MINUTES = 2 * 60 * 1000;

    @Inject
    protected MatchService matchService;

    @Inject
    protected GlobalMatchState globalMatchState;

    @Inject
    protected EntityRepository entityRepository;

    @Override
    protected void scheduledAction() {
        Date cutOfDate = new Date(System.currentTimeMillis() - TWO_MINUTES);

        for (Match match: entityRepository.find(getQuery(cutOfDate)).getHits()) {
            Optional<MatchStateWrapper> matchStateOptional = globalMatchState.getCurrentMatchState(match.getId());
            if (matchStateOptional.isEmpty()) {
                matchService.endMatch(match);
                break;
            }

            MatchStateWrapper matchStateWrapper = matchStateOptional.get();
            if (matchStateWrapper.getTimeStamp().before(cutOfDate)) {
                matchService.endMatch(match);
            }
        }
    }

    @FixedRate(initialDelay = 1, value = 1, timeUnit = TimeUnit.MINUTES)
    public void monitorSchedule() {
        super.runSchedule();
    }

    protected EntityQuery<Match> getQuery(Date cutOfDate) {
        return new EntityQuery<>(Match.class,
                0,
                null,
                List.of(
                        new ExplicitSearchCriteria(Match.C_IS_PLAYING,true),
                        new DateSearchCriteria(Match.C_START_DATE, null, cutOfDate)),
                Map.of(Match.C_START_DATE, false));
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
