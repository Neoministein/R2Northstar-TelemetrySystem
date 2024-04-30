package com.neo.r2.ts.impl.match.event.processor.player;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.match.state.PlayerStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class PlayerNewLoadoutEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public PlayerNewLoadoutEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return "PilotNewLoadout";
    }

    @Override
    protected String getSchemaName() {
        return "state/PilotNewLoadout.json";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        Optional<PlayerStateWrapper> optPlayer = matchState.getPlayer(event.getEntityId());
        if (optPlayer.isPresent()) {
            PlayerStateWrapper player = optPlayer.get();
            player.setPrimary(  event.get("primary").asText());
            player.setSecondary(event.get("secondary").asText());
            player.setWeapon3(  event.get("weapon3").asText());
            player.setSpecial(  event.get("special").asText());

            matchState.addEvent(getEventName(), event);
            saveSearchable(new MatchEventSearchable(matchState, getEventName(), player));
        }
    }
}
