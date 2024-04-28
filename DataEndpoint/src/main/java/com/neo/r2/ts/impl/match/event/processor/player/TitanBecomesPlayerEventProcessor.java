package com.neo.r2.ts.impl.match.event.processor.player;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.match.state.PlayerStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class TitanBecomesPlayerEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public TitanBecomesPlayerEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/BasicEvent.json";
    }

    @Override
    public String getEventName() {
        return "TitanBecomesPilot";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        Optional<PlayerStateWrapper> optPlayer = matchState.getPlayer(event.getEntityId());
        if (optPlayer.isPresent()) {
            PlayerStateWrapper player = optPlayer.get();
            player.setTitanClass(CustomConstants.UNKNOWN);
            player.setIsTitan(false);

            matchState.addEvent(getEventName(), event);
            saveSearchable(createBasicSearchable(event, matchState));
        }
    }
}

