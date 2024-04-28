package com.neo.r2.ts.api.match.event;

import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.networknt.schema.JsonSchema;

public interface MatchEventProcessor {

    String getEventName();

    JsonSchema getSchema();

    void processEvent(MatchEventWrapper event, MatchStateWrapper matchState);

    //List<Searchable> parseToSearchable(MatchEventWrapper event, MatchStateWrapper endMatchState);
}
