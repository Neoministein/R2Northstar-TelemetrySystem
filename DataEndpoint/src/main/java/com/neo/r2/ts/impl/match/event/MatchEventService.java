package com.neo.r2.ts.impl.match.event;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchStateSearchable;
import com.neo.r2.ts.web.socket.MatchStateOutputSocket;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class MatchEventService {

    @Inject
    protected GlobalMatchState globalMatchState;

    @Inject
    protected MatchEventBuffer matchEventBuffer;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected MatchStateOutputSocket matchStateOutputSocket;

    protected Map<String, MatchEventProcessor> eventProcessorMap = new HashMap<>();

    @Inject
    public MatchEventService(Instance<MatchEventProcessor> matchEventProcessors) {
        for (MatchEventProcessor processor: matchEventProcessors) {
            eventProcessorMap.put(processor.getEventName(), processor);
        }
    }

    public void delegateToEventProcessor(MatchEvent event) {
        MatchEventProcessor processor = eventProcessorMap.get(event.getEventType());
        if (processor == null) {
            throw new ValidationException(CustomConstants.EX_UNSUPPORTED_EVENT_TYPE, event);
        }

        JsonSchemaUtil.isValidOrThrow(event.getRawData(), processor.getSchema());

        MatchStateWrapper matchStateWrapper = globalMatchState.getCurrentMatchState(event.getMatchId()).orElseThrow();

        processor.handleIncomingEvent(event.getMatchId(), event, matchStateWrapper);

        matchEventBuffer.addToBuffer(event.getMatchId(), event);
        processor.updateMatchState(event, matchStateWrapper);
    }

    public void endMatchState(String matchId) {
        List<MatchEvent> events = matchEventBuffer.getBuffer(matchId);

        MatchStateWrapper matchStateWrapper = globalMatchState.getCurrentMatchState(matchId).orElseThrow();
        for (MatchEvent event: events) {
            searchProvider.index(eventProcessorMap.get(event.getEventType()).parseToSearchable(event, matchStateWrapper));
        }

        newMatchState(matchId);
        matchStateWrapper.clearEvents();

        matchStateWrapper.updateTimeStamp();
        for (MatchEvent event: events) {
            eventProcessorMap.get(event.getEventType()).cleanUpState(event, matchStateWrapper);
        }
    }

    public void newMatchState(String matchId) {
        Optional<MatchStateWrapper> matchState = globalMatchState.getCurrentMatchState(matchId);
        if (matchState.isPresent()) {
            matchStateOutputSocket.broadcast(matchId, JsonUtil.toJson(matchState.get().getState()));
            if (searchProvider.enabled()) {
                searchProvider.index(new MatchStateSearchable(matchState.get().getState().deepCopy()));
            }
        }

    }

    public int getNumberOfPlayerInMatch(String matchId) {
        return globalMatchState.getCurrentMatchState(matchId).map(MatchStateWrapper::getNumberOfPlayers).orElse(0);
    }
}
