package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.repository.searchable.MatchResultRepository;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class MatchResultEventProcessor extends AbstractBasicEventProcessor {

    protected final MatchResultRepository matchResultRepository;

    @Inject
    protected MatchResultEventProcessor(MatchResultRepository matchResultRepository ,JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
        this.matchResultRepository = matchResultRepository;
    }

    @Override
    public void handleIncomingEvent(String matchId, MatchEvent event, MatchStateWrapper matchStateWrapper) {
        matchResultRepository.saveResult(new MatchResultSearchable(event, matchStateWrapper));
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {

    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState) {
        return List.of();
    }

    @Override
    public void cleanUpState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {}

    @Override
    public String getEventName() {
        return "MatchResult";
    }

    @Override
    protected String getSchemaName() {
        return "state/MatchResult.json";
    }
}
