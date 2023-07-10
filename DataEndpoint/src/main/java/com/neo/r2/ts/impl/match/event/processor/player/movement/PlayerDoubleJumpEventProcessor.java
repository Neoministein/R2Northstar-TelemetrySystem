package com.neo.r2.ts.impl.match.event.processor.player.movement;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlayerDoubleJumpEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "PlayerDoubleJump";
    }
}

