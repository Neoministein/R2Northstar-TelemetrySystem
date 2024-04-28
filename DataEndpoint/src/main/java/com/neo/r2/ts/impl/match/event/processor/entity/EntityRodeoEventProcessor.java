package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractPlayerStateEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EntityRodeoEventProcessor extends AbstractPlayerStateEventProcessor implements MatchEventProcessor {

    @Inject
    public EntityRodeoEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/EntityRodeo.json";
    }

    @Override
    public String getEventName() {
        return "EntityRodeo";
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEventWrapper event, MatchStateWrapper endMatchState) {
        if (!saveSearchable()) {
            return List.of();
        }

        String entityId = event.get("entityId").asText();
        String rodeoEntityId = event.get("rodeoEntityId").asText();

        MatchEventSearchable searchable = new MatchEventSearchable(endMatchState, getEventName(),
                endMatchState.getEntity(entityId).orElse(parseBackupEntity(entityId)));
        searchable.setData(endMatchState.getEntity(rodeoEntityId).orElse(parseBackupEntity(rodeoEntityId)));
        return List.of(searchable);
    }

    @Override
    protected void setStateOnPlayer(JsonNode state, ObjectNode player) {
        player.set("isRodeoing", state);
    }
}
