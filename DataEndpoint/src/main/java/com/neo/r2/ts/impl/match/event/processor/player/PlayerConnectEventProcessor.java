package com.neo.r2.ts.impl.match.event.processor.player;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.player.PlayerLookUpService;
import com.neo.util.common.impl.json.JsonUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerConnectEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    protected PlayerLookUpService playerLookUpService;

    @Override
    public void handleIncomingEvent(String matchId, JsonNode event, MatchStateWrapper matchStateWrapper) {
        playerLookUpService.updatePlayerLookUp(event.get("entityId").asText(), event.get("playerName").asText());
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        ObjectNode player = JsonUtil.emptyObjectNode();
        String entityId = event.get("entityId").asText();

        player.put("entityId", entityId);
        player.put("entityType", "player");
        player.put("team", event.get("team").asInt());
        player.put("titanClass", CustomConstants.UNKNOWN);
        player.put("health", 100);
        player.put("isTitan", false);
        player.put("isWallRunning", false);
        player.put("isShooting", false);
        player.put("isGrounded", false);
        player.put("isHanging", false);
        player.put("isCrouching", false);
        player.put("isAlive", false);
        player.put("isRodeoing", false);

        player.put("/distance", 0);

        ObjectNode position = player.withObject("/position");
        position.put("x", 0);
        position.put("y", 0);
        position.put("z", 0);

        ObjectNode rotation = player.withObject("/rotation");
        rotation.put("x", 0);
        rotation.put("y", 0);
        rotation.put("z", 0);

        ObjectNode velocity = player.withObject("/velocity");
        velocity.put("x", 0);
        velocity.put("y", 0);
        velocity.put("z", 0);

        ObjectNode equipment = player.withObject("/equipment");
        equipment.put("primary", CustomConstants.UNKNOWN);
        equipment.put("secondary", CustomConstants.UNKNOWN);
        equipment.put("weapon3", CustomConstants.UNKNOWN);
        equipment.put("special", CustomConstants.UNKNOWN);

        matchStateToUpdate.addPlayer(entityId, player);
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
