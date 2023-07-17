package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Instant;
import java.util.*;

public class MatchStateWrapper {

    public static final String MATCH_ID = "matchId";
    public static final String MAP = "map";
    public static final String GAME_MODE = "gamemode";
    public static final String PLAYERS = "players";
    public static final String NPCS = "npcs";
    public static final String EVENTS = "events";
    public static final String TIME_PASSED = "timePassed";

    public static final String TAGS = "tags";

    private Map<String, ObjectNode> players = new HashMap<>();
    private Map<String, ObjectNode> npcs = new HashMap<>();

    private final ObjectNode matchState;
    private Instant timeStamp;

    public MatchStateWrapper(ObjectNode matchState) {
        this.matchState = matchState;
        this.timeStamp = Instant.now();
    }

    public String getMatchId() {
        return matchState.get(MATCH_ID).asText();
    }

    public String getMap() {
        return matchState.get(MAP).asText();
    }

    public int getTimePassed() {
        return matchState.get(TIME_PASSED).asInt();
    }

    public String getGameMode() {
        return matchState.get(GAME_MODE).asText();
    }

    public int getNumberOfPlayers() {
        return matchState.get(PLAYERS).size();
    }

    public Collection<ObjectNode> getPlayers() {
        return players.values();
    }

    public Collection<ObjectNode> getNpcs() {
        return npcs.values();
    }

    public Optional<ObjectNode> getEntity(String entityId) {
        return getPlayer(entityId).or(() -> getNpc(entityId));
    }

    public Optional<ObjectNode> getPlayer(String entityId) {
        return Optional.ofNullable(players.get(entityId));
    }

    public Optional<ObjectNode> getNpc(String entityId) {
        return Optional.ofNullable(npcs.get(entityId));
    }

    public void addPlayer(String entityId, ObjectNode player) {
        matchState.withArray(PLAYERS).add(player);
        players.put(entityId, player);
    }

    public void addNpc(String entityId, ObjectNode npc) {
        matchState.withArray(NPCS).add(npc);
        npcs.put(entityId, npc);
    }
    public Optional<ObjectNode> removePlayer(String entityId) {
        players.remove(entityId);
        return removeEntity(entityId, PLAYERS);
    }

    public Optional<ObjectNode> removeNpc(String entityId) {
        players.remove(entityId);
        return removeEntity(entityId, NPCS);
    }

    protected Optional<ObjectNode> removeEntity(String entityId, String qualifier) {
        for (int i = 0; i < matchState.withArray(qualifier).size(); i++) {
            JsonNode player = matchState.withArray(qualifier).get(i);
            if (player.get("entityId").asText().equals(entityId)) {
                matchState.withArray(qualifier).remove(i);
                return Optional.of((ObjectNode) player);
            }
        }

        return Optional.empty();
    }

    public void addEvent(String eventName, JsonNode event) {
        matchState.withObject("/" + EVENTS).withArray(eventName).add(event);
    }

    public void clearEvents(String eventName) {
        matchState.withObject("/" + EVENTS).withArray(eventName).removeAll();
    }

    public ObjectNode getState() {
        return matchState;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public List<String> getTags() {
        List<String> tags = new LinkedList<>();
        for (JsonNode tag: matchState.withArray(TAGS)) {
            tags.add(tag.asText());
        }
        return tags;
    }

    public void updateTimeStamp() {
        timeStamp = Instant.now();
    }
}
