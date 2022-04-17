package com.neo.tf2.gsp;

import com.neo.tf2.gsp.event.Event;
import com.neo.tf2.gsp.event.EventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameState {

    private final UUID id;
    private final String map;
    private final Map<String, Player> players;
    private List<Event> eventList = new ArrayList<>();


    public GameState(UUID id, String map, Map<String, Player> players) {
        this.id = id;
        this.map = map;
        this.players = players;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("map",map);
        json.put("timestamp", System.currentTimeMillis());

        JSONArray players = new JSONArray();
        for (Map.Entry<String, Player> player: this.players.entrySet()) {
            players.put(player.getValue().toJson());
        }
        json.put("players", players);

        JSONObject events = new JSONObject();
        for (EventType eventType: EventType.values()) {
            events.put(eventType.value, new JSONArray());
        }

        for (Event event: eventList) {
            events.getJSONArray(event.type().value).put(event.toJson());
        }
        json.put("events", events);

        return json;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
