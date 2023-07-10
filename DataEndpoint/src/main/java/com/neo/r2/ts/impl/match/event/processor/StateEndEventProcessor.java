package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class StateEndEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    public static final String STATE_END_EVENT = "StateEnd";

    @Inject
    protected MatchEventService matchEventService;

    @Override
    public void handleIncomingEvent(String matchId, JsonNode event, MatchStateWrapper matchStateWrapper) {
        matchEventService.endMatchState(matchId);
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getState().set("timePassed", event.get("timePassed"));
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
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
