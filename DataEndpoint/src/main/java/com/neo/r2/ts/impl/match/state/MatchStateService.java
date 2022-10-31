package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.persistence.searchable.MatchEvent;
import com.neo.r2.ts.impl.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.impl.persistence.searchable.MatchStateSearchable;
import com.neo.r2.ts.impl.socket.MatchStateOutputSocket;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class MatchStateService {

    protected static final String BROADCAST_END = "MATCH_END";

    protected static final List<MatchEvent> BASIC_MATCH_EVENTS = List.of(
            MatchEvent.CONNECT,
            MatchEvent.DISCONNECT,
            MatchEvent.PLAYER_RESPAWNED,
            MatchEvent.PILOT_BECOMES_TITAN,
            MatchEvent.TITAN_BECOMES_PILOT,
            MatchEvent.JUMP,
            MatchEvent.DOUBLE_JUMP,
            MatchEvent.MANTLE,
            MatchEvent.NEW_LOADOUT);

    protected final boolean shouldSaveNpcPosition;

    @Inject
    protected SearchRepository searchRepository;

    @Inject
    protected GlobalMatchState globalGameState;

    @Inject
    protected MatchStateOutputSocket matchStateOutputSocket;

    @Inject
    public MatchStateService(ConfigService configService) {
        shouldSaveNpcPosition = configService.get("r2ts.shouldSaveNpcPosition").asBoolean().orElse(false);
    }

    public void updateGameState(JsonNode matchState) {
        MatchStateWrapper matchStateWrapper = new MatchStateWrapper(matchState);

        String matchId = matchState.get("matchId").asText();
        matchStateOutputSocket.broadcast(matchId, JsonUtil.toJson(matchState));
        globalGameState.setCurrentMatchState(matchId, matchStateWrapper);
        if (searchRepository.enabled()) {
            MatchStateSearchable searchable = new MatchStateSearchable(matchState.deepCopy());
            searchRepository.index(searchable);
            searchRepository.index(parseStateToEvents(matchState));
        }
    }

    public void matchEnded(UUID matchId) {
        matchStateOutputSocket.broadcast(matchId.toString(), BROADCAST_END);
        globalGameState.removeGameState(matchId);
    }

    public int getNumberOfPlayerInMatch(UUID matchId) {
        return globalGameState.getCurrentMatchState(matchId).map(MatchStateWrapper::getNumberOfPlayers).orElse(0);
    }

    protected List<MatchEventSearchable> parseStateToEvents(JsonNode state) {
        Map<String, JsonNode> players = new HashMap<>();
        Map<String, JsonNode> npcs = new HashMap<>();
        List<MatchEventSearchable> matchEventList = new ArrayList<>();
        for (JsonNode p: state.get(MatchStateSearchable.F_PLAYERS)) {
            ObjectNode player = p.deepCopy();
            players.put(player.get(MatchStateSearchable.F_ENTITY_ID).asText(), player);
            MatchEventSearchable matchEvent = new MatchEventSearchable(state, MatchEvent.PLAYER_POSITION);
            matchEvent.setEntity(player);
            matchEventList.add(matchEvent);
        }
        for (JsonNode p: state.get(MatchStateSearchable.F_NPC)) {
            ObjectNode npc = p.deepCopy();
            npcs.put(npc.get(MatchStateSearchable.F_ENTITY_ID).asText(), npc);
            MatchEventSearchable matchEvent = new MatchEventSearchable(state, MatchEvent.NPC_POSITION);
            matchEvent.setEntity(npc);
            if (shouldSaveNpcPosition) {
                matchEventList.add(matchEvent);
            }
        }

        matchEventList.addAll(parseBasicEvents(players, state));
        matchEventList.addAll(parseKilledEvent(state, players, npcs));
        matchEventList.addAll(parseNpcLeechedEvent(state,players, npcs));
        return matchEventList;
    }

    protected List<MatchEventSearchable> parseBasicEvents(Map<String, JsonNode> players, JsonNode state) {
        List<MatchEventSearchable> basicEvents = new ArrayList<>();
        for (MatchEvent basicEvent : BASIC_MATCH_EVENTS) {
            for (JsonNode event: state.get(MatchStateSearchable.F_EVENTS).get(basicEvent.fieldName)) {
                MatchEventSearchable matchEvent = new MatchEventSearchable(state, basicEvent);
                matchEvent.setEntity(players.get(event.get(MatchStateSearchable.F_ENTITY_ID).asText()));
                basicEvents.add(matchEvent);
            }
        }
        return basicEvents;
    }

    protected List<MatchEventSearchable> parseKilledEvent(JsonNode matchState, Map<String, JsonNode> players, Map<String, JsonNode> npcs) {
        List<MatchEventSearchable> killedEvent = new ArrayList<>();

        for (JsonNode event: matchState.get(MatchStateSearchable.F_EVENTS).get(MatchEvent.ENTITY_KILLED.fieldName)) {
            MatchEventSearchable matchEvent = new MatchEventSearchable(matchState, MatchEvent.ENTITY_KILLED);
            if (event.get(MatchStateSearchable.F_IS_ATTACKER_PLAYER).asBoolean()) {
                matchEvent.setEntity(players.get(event.get(MatchStateSearchable.F_ATTACKER).asText()));
            } else {
                matchEvent.setEntity(npcs.get(event.get(MatchStateSearchable.F_ATTACKER).asText()));
            }
            ObjectNode data = JsonUtil.emptyObjectNode();
            if (event.get(MatchStateSearchable.F_IS_VICTIM_PLAYER).asBoolean()) {
                data.set(MatchEventSearchable.F_VICTIM, players.get(event.get(MatchStateSearchable.F_VICTIM).asText()));
            } else {
                data.set(MatchEventSearchable.F_VICTIM, npcs.get(event.get(MatchStateSearchable.F_VICTIM).asText()));
            }
            data.put(MatchStateSearchable.F_DAMAGE_TYPE, event.get(MatchStateSearchable.F_DAMAGE_TYPE).asText());

            matchEvent.setData(data);
            killedEvent.add(matchEvent);
        }
        return killedEvent;
    }

    protected List<MatchEventSearchable> parseNpcLeechedEvent(JsonNode matchState, Map<String, JsonNode> players, Map<String, JsonNode> npcs) {
        List<MatchEventSearchable> npcLeeched = new ArrayList<>();
        for (JsonNode event: matchState.get(MatchStateSearchable.F_EVENTS).get(MatchEvent.NPC_LEECHED.fieldName)) {
            MatchEventSearchable matchEvent = new MatchEventSearchable(matchState, MatchEvent.NPC_LEECHED);
            matchEvent.setEntity(players.get(event.get(MatchStateSearchable.F_PLAYER_ID).asText()));
            matchEvent.setData(npcs.get(event.get(MatchStateSearchable.F_NCP_ID).asText()));
            npcLeeched.add(matchEvent);
        }
        return npcLeeched;
    }
}
