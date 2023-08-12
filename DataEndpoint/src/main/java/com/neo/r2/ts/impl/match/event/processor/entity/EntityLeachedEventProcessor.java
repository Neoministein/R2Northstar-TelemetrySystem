package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EntityLeachedEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/EntityLeached.json";
    }

    @Override
    public String getEventName() {
        return "EntityLeached";
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        if (!saveSearchable()) {
            return List.of();
        }

        Optional<ObjectNode> player = endMatchState.getPlayer(event.get("entityId").asText());
        MatchEventSearchable searchable = new MatchEventSearchable(endMatchState, getEventName(), player.orElse(null));
        endMatchState.getNpc(event.get("specterId").asText()).ifPresent(searchable::setData);
        return List.of(searchable);
    }
}
