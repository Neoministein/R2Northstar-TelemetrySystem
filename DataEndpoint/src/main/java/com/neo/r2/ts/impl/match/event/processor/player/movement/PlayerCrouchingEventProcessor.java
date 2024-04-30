package com.neo.r2.ts.impl.match.event.processor.player.movement;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractPlayerStateEventProcessor;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.PlayerStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerCrouchingEventProcessor extends AbstractPlayerStateEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerCrouchingEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return "PlayerCrouching";
    }

    @Override
    protected void setStateOnPlayer(boolean state, EntityStateWrapper entity) {
        if (entity instanceof PlayerStateWrapper player) {
            player.setIsCrouching(state);
        }
    }
}
