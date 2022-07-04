package com.neo.r2.ts.impl.persistence;

import com.fasterxml.jackson.databind.JsonNode;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class GlobalGameState {

    protected Map<UUID, JsonNode> currentMatchState = new HashMap<>();

    public void setCurrentMatchState(JsonNode state) {
        currentMatchState.put(UUID.fromString(state.get("matchId").asText()), state);
    }

    public JsonNode getCurrentMatchState(UUID uuid) {
        return currentMatchState.get(uuid);
    }

    public void removeGameState(UUID uuid) {
        currentMatchState.remove(uuid);
    }
}
