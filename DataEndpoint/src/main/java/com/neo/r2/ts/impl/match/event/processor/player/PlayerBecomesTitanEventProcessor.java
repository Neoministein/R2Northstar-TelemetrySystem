package com.neo.r2.ts.impl.match.event.processor.player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class PlayerBecomesTitanEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerBecomesTitanEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "PilotBecomesTitan";
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> player = matchStateToUpdate.getPlayer(event.get("entityId").asText());
        if (player.isPresent()) {
            player.get().put("titanClass", event.get("titanClass").asText());
            player.get().put("isTitan", true);
        }
    }
}

