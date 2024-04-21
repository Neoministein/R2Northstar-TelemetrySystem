package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class StateEndEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    public static final String STATE_END_EVENT = "StateEnd";

    protected final MatchEventService matchEventService;

    @Inject
    protected StateEndEventProcessor(MatchEventService matchEventService, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
        this.matchEventService = matchEventService;
    }

    @Override
    public void handleIncomingEvent(String matchId, MatchEvent event, MatchStateWrapper matchStateWrapper) {
        matchEventService.endMatchState(matchId);
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.updateTimeStamp();
        matchStateToUpdate.getState().set("timePassed", event.get("timePassed"));
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState) {
        return List.of();
    }

    @Override
    public String getEventName() {
        return STATE_END_EVENT;
    }

    @Override
    protected String getSchemaName() {
        return "state/StateEndEvent.json";
    }
}
