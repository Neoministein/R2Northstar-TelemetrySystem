package com.neo.r2.ts.impl.match.state;

import com.neo.util.framework.api.cache.Cache;
import com.neo.util.framework.api.cache.spi.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class GlobalMatchState {

    @Inject
    @CacheName("currentMatchState")
    protected Cache currentMatchState;

    public void setCurrentMatchState(String matchId, MatchStateWrapper state) {
        currentMatchState.put(matchId, state);
    }

    public Optional<MatchStateWrapper> getCurrentMatchState(String matchId) {
        return currentMatchState.get(matchId);
    }

    public void removeGameState(String matchState) {
        currentMatchState.invalidate(matchState);
    }
}
