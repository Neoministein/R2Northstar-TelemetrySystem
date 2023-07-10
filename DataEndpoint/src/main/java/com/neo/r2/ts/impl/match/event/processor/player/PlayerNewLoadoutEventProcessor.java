package com.neo.r2.ts.impl.match.event.processor.player;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PlayerNewLoadoutEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    public String getEventName() {
        return "PilotNewLoadout";
    }

    @Override
    protected String getSchemaName() {
        return "state/PilotNewLoadout.json";
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> optPlayer = matchStateToUpdate.getPlayer(event.get("entityId").asText());
        if (optPlayer.isPresent()) {
            ObjectNode equipment = optPlayer.get().with("equipment");
            equipment.set("primary",   event.get("primary"));
            equipment.set("secondary", event.get("secondary"));
            equipment.set("weapon3",   event.get("weapon3"));
            equipment.set("special",   event.get("special"));
        }
    }
}
