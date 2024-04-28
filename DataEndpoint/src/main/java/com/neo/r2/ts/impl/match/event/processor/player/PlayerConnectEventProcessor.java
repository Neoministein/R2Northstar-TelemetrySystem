package com.neo.r2.ts.impl.match.event.processor.player;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.match.state.PlayerStateWrapper;
import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerConnectEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    protected final PlayerLookUpRepository playerLookUpRepository;

    @Inject
    public PlayerConnectEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService, PlayerLookUpRepository playerLookUpRepository) {
        super(searchProvider, jsonSchemaLoader, configService);
        this.playerLookUpRepository = playerLookUpRepository;
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        matchState.addEvent(getEventName(), event);

        matchState.addPlayer(new PlayerStateWrapper(event.getRawData()));
        playerLookUpRepository.updatePlayerLookUp(event.getEntityId(), event.get("playerName").asText());
    }

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "PlayerConnect";
    }
}
