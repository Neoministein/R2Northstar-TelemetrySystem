package com.neo.r2.ts.impl.match.event.processor.npc;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NpcDespawnEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "NpcDespawn";
    }

    @Override
    public void cleanUpState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        super.cleanUpState(event, matchStateToUpdate);
        matchStateToUpdate.removeNpc(event.get("entityId").asText());
    }

    @Override
    public boolean shouldBeAddedToMatchState() {
        return false;
    }
}
