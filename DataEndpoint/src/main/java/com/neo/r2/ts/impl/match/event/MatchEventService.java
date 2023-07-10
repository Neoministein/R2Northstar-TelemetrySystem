package com.neo.r2.ts.impl.match.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.state.GlobalMatchState;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MatchEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventService.class);

    @Inject
    protected GlobalMatchState globalMatchState;

    @Inject
    protected MatchEventBuffer matchEventBuffer;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected MatchStateService matchStateService;

    protected Map<String, MatchEventProcessor> eventProcessorMap = new HashMap<>();

    @Inject
    protected void init(Instance<MatchEventProcessor> matchEventProcessors) {
        for (MatchEventProcessor processor: matchEventProcessors) {
            eventProcessorMap.put(processor.getEventName(), processor);
        }
    }

    public void delegateToEventProcessor(String matchId, JsonNode event) {
        String eventType = event.get("eventType").asText();
        LOGGER.info("{}", eventType);

        MatchEventProcessor processor = eventProcessorMap.get(eventType);
        if (processor == null) {
            throw new IllegalStateException("Event type [" + eventType + "] does not exist");
        }

        JsonSchemaUtil.isValidOrThrow(event, processor.getSchema());

        MatchStateWrapper matchStateWrapper = globalMatchState.getCurrentMatchState(matchId).orElseThrow();

        processor.handleIncomingEvent(matchId, event, matchStateWrapper);

        matchEventBuffer.addToBuffer(matchId, event);
        processor.updateMatchState(event, matchStateWrapper);
    }

    public void endMatchState(String matchId) {
        List<JsonNode> events = matchEventBuffer.getBuffer(matchId);

        MatchStateWrapper matchStateWrapper = globalMatchState.getCurrentMatchState(matchId).orElseThrow();
        for (JsonNode event: events) {
            String eventType = event.get("eventType").asText();
            searchProvider.index(eventProcessorMap.get(eventType).parseToSearchable(event, matchStateWrapper));
        }

        matchStateService.newMatchState(matchId);

        matchStateWrapper.updateTimeStamp();
        for (JsonNode event: events) {
            String eventType = event.get("eventType").asText();
            eventProcessorMap.get(eventType).cleanUpState(event, matchStateWrapper);
        }
    }

    public Collection<MatchEventProcessor> getAllProcessors() {
        return eventProcessorMap.values();
    }
}
