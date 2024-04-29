package com.neo.r2.ts.impl.match.event.processor.npc;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractPositionEventProcessor;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class NpcPositionEventProcessor extends AbstractPositionEventProcessor implements MatchEventProcessor {

    public static final String EVENT_NAME = "NpcPosition";

    @Inject
    protected NpcPositionEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    protected Optional<EntityStateWrapper> getEntity(MatchStateWrapper matchStateToUpdate, String entityId) {
        return matchStateToUpdate.getNpc(entityId);
    }
}
