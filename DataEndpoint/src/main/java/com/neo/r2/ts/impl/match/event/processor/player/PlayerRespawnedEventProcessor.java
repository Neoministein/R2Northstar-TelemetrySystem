package com.neo.r2.ts.impl.match.event.processor.player;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PlayerRespawnedEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "PilotRespawned";
    }


    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> player = matchStateToUpdate.getPlayer(event.get("entityId").asText());
        player.ifPresent(x -> x.put("isAlive", true));
    }
}
