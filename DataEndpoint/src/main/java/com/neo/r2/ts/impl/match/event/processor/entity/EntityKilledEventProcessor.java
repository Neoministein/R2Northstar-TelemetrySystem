package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.web.rss.PlayerKillsRssFeed;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EntityKilledEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    public static final String DAMAGE_TYPE = "damageType";

    protected final PlayerKillsRssFeed playerKillsRssFeed;

    @Inject
    protected EntityKilledEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService, PlayerKillsRssFeed playerKillsRssFeed) {
        super(jsonSchemaLoader, configService);
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
    public void handleIncomingEvent(String matchId, MatchEvent event, MatchStateWrapper matchStateWrapper) {
        String attackerId = event.get("attackerId").asText();
        String victimId = event.get("victimId").asText();

        if (matchStateWrapper.getPlayer(attackerId).isPresent() && matchStateWrapper.getPlayer(victimId).isPresent()) {
            String damageType = event.get(DAMAGE_TYPE).asText();
            playerKillsRssFeed.addPlayerKill(attackerId, victimId, damageType);
        }

    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> victimPlayer = matchStateToUpdate.getPlayer(event.get("victimId").asText());
        if (victimPlayer.isPresent()) {
            ObjectNode player = victimPlayer.get();
            player.put("isTitan", false);
            player.put("isWallRunning", false);
            player.put("isShooting", false);
            player.put("isGrounded", false);
            player.put("isHanging", false);
            player.put("isCrouching", false);
            player.put("isAlive", false);
            player.put("isRodeoing", false);
            player.put("isTitan", CustomConstants.UNKNOWN);
        }
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState) {
        if (!saveSearchable()) {
            return List.of();
        }

        ObjectNode data = JsonUtil.emptyObjectNode();
        String victimId = event.get("victimId").asText();
        String attackerId = event.get("attackerId").asText();
        ObjectNode victim = endMatchState.getEntity(victimId).orElse(null);

        data.set("victim", victim);
        data.put(DAMAGE_TYPE, event.get(DAMAGE_TYPE).asText());

        MatchEventSearchable searchable = new MatchEventSearchable(endMatchState, getEventName(),
                endMatchState.getEntity(attackerId).orElse(null));
        searchable.setData(data);
        return List.of(searchable);
    }
}
