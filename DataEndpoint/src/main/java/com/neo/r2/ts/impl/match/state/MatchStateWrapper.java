package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.common.impl.json.JsonUtil;

import java.time.Duration;
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
    public static final String MATCH_TIME = "matchTime";

    public static final String TAGS = "tags";

    private final Map<String, PlayerStateWrapper> players = new HashMap<>();
    private final Map<String, EntityStateWrapper> npcs = new HashMap<>();

    private final ObjectNode matchState;
    private final Instant startTime;
    private Instant lastUpdated;

    public MatchStateWrapper(Match match) {
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();

        this.matchState = JsonUtil.emptyObjectNode();
        matchState.put(MAP, match.getMap());
        matchState.put(MATCH_ID, match.getStringId());
        matchState.put(GAME_MODE, match.getGamemode());

        ArrayNode tags = matchState.putArray(TAGS);
        match.getTags().forEach(tags::add);
        matchState.put(TIME_PASSED, 0);

        matchState.putArray(PLAYERS);
        matchState.putArray(NPCS);
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

    public Collection<PlayerStateWrapper> getPlayers() {
        return players.values();
    }

    public Collection<EntityStateWrapper> getNpcs() {
        return npcs.values();
    }

    public Optional<EntityStateWrapper> getEntity(String entityId) {
        return getNpc(entityId).or(() -> getPlayer(entityId));
    }

    public Optional<PlayerStateWrapper> getPlayer(String entityId) {
        return Optional.ofNullable(players.get(entityId));
    }

    public Optional<EntityStateWrapper> getNpc(String entityId) {
        return Optional.ofNullable(npcs.get(entityId));
    }

    public void addPlayer(PlayerStateWrapper player) {
        matchState.withArray(PLAYERS).add(player.getRawData());
        players.put(player.getEntityId(), player);
    }

    public void addNpc(EntityStateWrapper npc) {
        matchState.withArray(NPCS).add(npc.getRawData());
        npcs.put(npc.getEntityId(), npc);
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

    public void addEvent(String eventName, MatchEventWrapper event) {
        matchState.withObject("/" + EVENTS).withArray(eventName).add(event.getRawData());
    }

    public void clearEvents() {
        matchState.remove("/" + EVENTS);
    }

    public ObjectNode getState() {
        return matchState;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public List<String> getTags() {
        List<String> tags = new LinkedList<>();
        for (JsonNode tag: matchState.withArray(TAGS)) {
            tags.add(tag.asText());
        }
        return tags;
    }

    public void updateTimeStamp(int matchTime) {
        lastUpdated = Instant.now();
        matchState.put(TIME_PASSED, Duration.between(startTime, lastUpdated).toMillis());
        matchState.put(MATCH_TIME, matchTime);
    }
}
