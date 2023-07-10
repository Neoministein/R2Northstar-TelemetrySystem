package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;

public abstract class AbstractStateEventProcessor extends AbstractBasicEventProcessor {

    protected abstract void setStateOnPlayer(JsonNode state, ObjectNode player);

    @Override
    protected String getSchemaName() {
        return "state/BasicStateEvent.json";
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getPlayer(event.get("entityId").asText()).ifPresent(p ->
                setStateOnPlayer(event.get("state"), p));
    }

    @Override
    public boolean shouldBeAddedToMatchState() {
        return false;
    }
}
