package com.neo.r2.ts.impl.match.event.processor.npc;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NpcDespawnEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public NpcDespawnEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "NpcDespawn";
    }

    @Override
    public void cleanUpState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.cleanUpState(event, matchStateToUpdate);
        matchStateToUpdate.removeNpc(event.get("entityId").asText());
    }
}
