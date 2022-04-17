package com.neo.tf2.gsp.event;

import org.json.JSONObject;

public class PlayerBecomesTitan extends BasicPlayerEvent{

    private final String titanClass;

    public PlayerBecomesTitan(String playerId, String titanClass) {
        super(playerId, EventType.PLAYER_BECOMES_TITAN);
        this.titanClass = titanClass;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("titanClass", titanClass);

        return json;
    }
}
