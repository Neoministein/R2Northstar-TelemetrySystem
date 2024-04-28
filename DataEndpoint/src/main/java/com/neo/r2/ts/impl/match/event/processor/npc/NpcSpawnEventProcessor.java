package com.neo.r2.ts.impl.match.event.processor.npc;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NpcSpawnEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public NpcSpawnEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "NpcSpawn";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.addNpc(new EntityStateWrapper(event.get("entityType").asText(), event.getRawData()));
    }
}
