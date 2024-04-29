package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;

public abstract class AbstractPlayerStateEventProcessor extends AbstractBasicEventProcessor {

    protected AbstractPlayerStateEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    protected abstract void setStateOnPlayer(boolean state, EntityStateWrapper entity);

    @Override
    protected String getSchemaName() {
        return "state/BasicStateEvent.json";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getEntity(event.get("entityId").asText()).ifPresent(p -> setStateOnPlayer(event.get("state").asBoolean(), p));
    }
}
