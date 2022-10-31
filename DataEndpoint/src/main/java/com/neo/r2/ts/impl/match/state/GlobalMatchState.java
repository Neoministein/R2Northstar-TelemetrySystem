package com.neo.r2.ts.impl.match.state;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GlobalMatchState {

    protected Map<UUID, MatchStateWrapper> currentMatchState = new HashMap<>();

    public void setCurrentMatchState(String matchId, MatchStateWrapper state) {
        currentMatchState.put(UUID.fromString(matchId), state);
    }

    public Optional<MatchStateWrapper> getCurrentMatchState(UUID uuid) {
        return Optional.ofNullable(currentMatchState.get(uuid));
    }

    public void removeGameState(UUID uuid) {
        currentMatchState.remove(uuid);
    }
}
