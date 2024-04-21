package com.neo.r2.ts.api.match.event;

import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.networknt.schema.JsonSchema;

import java.util.List;

public interface MatchEventProcessor {

    String getEventName();

    JsonSchema getSchema();

    void handleIncomingEvent(String matchId, MatchEvent event, MatchStateWrapper endMatchState);

    void updateMatchState(MatchEvent event, MatchStateWrapper endMatchState);

    List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState);

    void cleanUpState(MatchEvent event, MatchStateWrapper matchStateToUpdate);
}
