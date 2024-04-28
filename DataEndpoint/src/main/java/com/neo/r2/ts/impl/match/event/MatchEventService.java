package com.neo.r2.ts.impl.match.event;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class MatchEventService {

    protected final GlobalMatchState globalMatchState;

    protected Map<String, MatchEventProcessor> eventProcessorMap = new HashMap<>();

    @Inject
    public MatchEventService(GlobalMatchState globalMatchState, Instance<MatchEventProcessor> matchEventProcessors) {
        this.globalMatchState = globalMatchState;

        for (MatchEventProcessor processor: matchEventProcessors) {
            eventProcessorMap.put(processor.getEventName(), processor);
        }
    }

    public void processIncomingEvent(MatchEventWrapper event) {
        MatchEventProcessor processor = eventProcessorMap.get(event.getEventType());
        if (processor == null) {
            throw new ValidationException(CustomConstants.EX_UNSUPPORTED_EVENT_TYPE, event.getEventType());
        }

        JsonSchemaUtil.isValidOrThrow(event.getRawData(), processor.getSchema());

        processor.processEvent(event, globalMatchState.requestCurrentMatchState(event.getMatchId()));
    }

    public int getNumberOfPlayerInMatch(String matchId) {
        return globalMatchState.getCurrentMatchState(matchId).map(MatchStateWrapper::getNumberOfPlayers).orElse(0);
    }
}
