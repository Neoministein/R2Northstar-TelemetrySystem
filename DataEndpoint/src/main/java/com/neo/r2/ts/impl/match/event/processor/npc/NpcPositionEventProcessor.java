package com.neo.r2.ts.impl.match.event.processor.npc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractPositionEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class NpcPositionEventProcessor extends AbstractPositionEventProcessor implements MatchEventProcessor {

    public static final String EVENT_NAME = "NpcPosition";

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    protected Optional<ObjectNode> getEntity(MatchStateWrapper matchStateToUpdate, String entityId) {
        return matchStateToUpdate.getNpc(entityId);
    }

    @Override
    protected Collection<ObjectNode> getAllEntities(MatchStateWrapper matchStateWrapper) {
        return matchStateWrapper.getNpcs();
    }
}
