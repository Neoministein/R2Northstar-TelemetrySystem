package com.neo.tf2.gsp.event;

import org.json.JSONObject;

public class BasicPlayerEvent implements Event{

    private final String playerId;
    private final EventType type;

    public BasicPlayerEvent(String playerId, EventType type) {
        this.playerId = playerId;
        this.type = type;
    }

    public String getPlayerId() {
        return playerId;
    }

    @Override
    public EventType type() {
        return type;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("player", playerId);
        return json;
    }
}
