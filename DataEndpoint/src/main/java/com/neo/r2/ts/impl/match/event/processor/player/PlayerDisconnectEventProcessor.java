package com.neo.r2.ts.impl.match.event.processor.player;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerDisconnectEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerDisconnectEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "PlayerDisconnect";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        matchState.addEvent(getEventName(), event);
        matchState.removePlayer(event.get("entityId").asText());
    }
}
