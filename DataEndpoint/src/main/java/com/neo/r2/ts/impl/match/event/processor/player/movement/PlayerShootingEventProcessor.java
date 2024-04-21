package com.neo.r2.ts.impl.match.event.processor.player.movement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractStateEventProcessor;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerShootingEventProcessor extends AbstractStateEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerShootingEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return "PlayerShooting";
    }

    @Override
    protected void setStateOnPlayer(JsonNode state, ObjectNode player) {
        player.set("isShooting", state);
    }
}
