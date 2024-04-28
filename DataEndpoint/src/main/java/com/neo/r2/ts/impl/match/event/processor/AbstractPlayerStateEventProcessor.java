package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;

public abstract class AbstractPlayerStateEventProcessor extends AbstractBasicEventProcessor {

    protected AbstractPlayerStateEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    protected abstract void setStateOnPlayer(JsonNode state, ObjectNode player);

    @Override
    protected String getSchemaName() {
        return "state/BasicStateEvent.json";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getPlayer(event.get("entityId").asText()).ifPresent(p ->
                setStateOnPlayer(event.get("state"), p.getRawData()));
    }
}
