package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.r2.ts.persistence.searchable.MatchStateSearchable;
import com.neo.r2.ts.web.socket.MatchStateOutputSocket;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MatchStateService {

    protected static final String BROADCAST_END = "MATCH_END";

    protected final boolean shouldSaveNpcPosition;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected GlobalMatchState globalGameState;

    @Inject
    protected MatchStateOutputSocket matchStateOutputSocket;

    @Inject
    protected MatchEventService matchEventService;

    @Inject
    public MatchStateService(ConfigService configService) {
        shouldSaveNpcPosition = configService.get("r2ts.shouldSaveNpcPosition").asBoolean().orElse(false);
    }

    public void initializeMatchState(Match match) {
        ObjectNode matchState = JsonUtil.emptyObjectNode();
        matchState.put(MatchStateWrapper.MAP, match.getMap());
        matchState.put(MatchStateWrapper.MATCH_ID, match.getId().toString());
        matchState.put(MatchStateWrapper.GAME_MODE, match.getGamemode());
        ArrayNode tags = matchState.putArray(MatchStateWrapper.TAGS);
        match.getTags().forEach(tags::add);
        matchState.put(MatchStateWrapper.TIME_PASSED, 0);
        matchState.putArray(MatchStateWrapper.PLAYERS);
        matchState.putArray(MatchStateWrapper.NPCS);
        ObjectNode events = matchState.withObject("/" + MatchStateWrapper.EVENTS);

        for (MatchEventProcessor processor: matchEventService.getAllProcessors()) {
            if (processor.shouldBeAddedToMatchState()) {
                events.withArray(processor.getEventName());
            }
        }

        globalGameState.setCurrentMatchState(match.getId().toString(), new MatchStateWrapper(matchState));
    }

    public void newMatchState(String matchId) {
        Optional<MatchStateWrapper> matchState = globalGameState.getCurrentMatchState(matchId);
        if (matchState.isPresent()) {
            matchStateOutputSocket.broadcast(matchId, JsonUtil.toJson(matchState.get().getState()));
            if (searchProvider.enabled()) {
                searchProvider.index(new MatchStateSearchable(matchState.get().getState().deepCopy()));
            }
        }

    }

    public void matchEnded(String matchId) {
        matchStateOutputSocket.broadcast(matchId, BROADCAST_END);
        globalGameState.removeGameState(matchId);
    }

    public int getNumberOfPlayerInMatch(UUID matchId) {
        return globalGameState.getCurrentMatchState(matchId.toString()).map(MatchStateWrapper::getNumberOfPlayers).orElse(0);
    }
}
