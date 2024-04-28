package com.neo.r2.ts.impl.match.event.processor.player.movement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractPlayerStateEventProcessor;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerWallRunningEventProcessor extends AbstractPlayerStateEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerWallRunningEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return "PlayerWallRunning";
    }

    @Override
    protected void setStateOnPlayer(JsonNode state, ObjectNode player) {
        player.set("isWallRunning", state);
    }
}
