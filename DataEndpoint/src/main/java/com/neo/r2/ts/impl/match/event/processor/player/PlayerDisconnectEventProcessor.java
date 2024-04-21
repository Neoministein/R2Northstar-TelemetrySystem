package com.neo.r2.ts.impl.match.event.processor.player;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlayerDisconnectEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "PlayerDisconnect";
    }

    @Override
    public void cleanUpState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.cleanUpState(event, matchStateToUpdate);
        matchStateToUpdate.removePlayer(event.get("entityId").asText());
    }
}
