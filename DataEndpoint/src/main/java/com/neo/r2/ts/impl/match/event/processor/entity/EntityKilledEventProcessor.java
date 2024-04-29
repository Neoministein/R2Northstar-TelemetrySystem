package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.match.state.PlayerStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.web.rss.PlayerKillsRssFeed;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class EntityKilledEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    public static final String DAMAGE_TYPE = "damageType";

    protected final PlayerKillsRssFeed playerKillsRssFeed;

    @Inject
    protected EntityKilledEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService, PlayerKillsRssFeed playerKillsRssFeed) {
        super(searchProvider, jsonSchemaLoader, configService);
        this.playerKillsRssFeed = playerKillsRssFeed;
    }

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "EntityKilled";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        matchState.addEvent(getEventName(), event);

        String attackerId = event.get("attackerId").asText();
        String victimId = event.get("victimId").asText();

        //Only if player set to killed, entities get directly removed by the game
        Optional<PlayerStateWrapper> victimPlayer = matchState.getPlayer(victimId);
        if (victimPlayer.isPresent()) {
            PlayerStateWrapper player = victimPlayer.get();
            player.killed();
        }

        Optional<EntityStateWrapper> attacker = matchState.getEntity(attackerId);
        Optional<EntityStateWrapper> victim = matchState.getEntity(victimId);

        //Send to Rss Feed
        if (attacker.isPresent() && victim.isPresent()) {
            String damageType = event.get(DAMAGE_TYPE).asText();
            playerKillsRssFeed.addPlayerKill(attackerId, victimId, damageType);
        }

        //Parse and send Searchable
        ObjectNode data = JsonUtil.emptyObjectNode();
        data.set("victim", victim.map(EntityStateWrapper::getRawData).orElse(null));
        data.put(DAMAGE_TYPE, event.get(DAMAGE_TYPE).asText());

        saveSearchable(new MatchEventSearchable(matchState, getEventName(), attacker.map(EntityStateWrapper::getRawData).orElse(null), data));
    }
}
