package com.neo.r2.ts.impl.match.event.processor.player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TitanBecomesPlayerEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "TitanBecomesPilot";
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> player = matchStateToUpdate.getPlayer(event.get("entityId").asText());
        if (player.isPresent()) {
            player.get().put("titanClass", CustomConstants.UNKNOWN);
            player.get().put("isTitan", false);
        }
    }
}

