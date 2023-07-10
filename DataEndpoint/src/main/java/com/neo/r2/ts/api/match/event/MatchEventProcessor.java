package com.neo.r2.ts.api.match.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.networknt.schema.JsonSchema;

import java.util.List;

public interface MatchEventProcessor {

    String getEventName();

    JsonSchema getSchema();

    void handleIncomingEvent(String matchId, JsonNode event, MatchStateWrapper endMatchState);

    void updateMatchState(JsonNode event, MatchStateWrapper endMatchState);

    List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState);

    void cleanUpState(JsonNode event, MatchStateWrapper matchStateToUpdate);

    default boolean shouldBeAddedToMatchState() {
        return true;
    }
}
