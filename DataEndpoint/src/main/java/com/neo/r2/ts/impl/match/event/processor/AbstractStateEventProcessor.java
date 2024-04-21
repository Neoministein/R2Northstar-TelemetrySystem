package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;

public abstract class AbstractStateEventProcessor extends AbstractBasicEventProcessor {

    protected AbstractStateEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    protected abstract void setStateOnPlayer(JsonNode state, ObjectNode player);

    @Override
    protected String getSchemaName() {
        return "state/BasicStateEvent.json";
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getPlayer(event.get("entityId").asText()).ifPresent(p ->
                setStateOnPlayer(event.get("state"), p));
    }
}
