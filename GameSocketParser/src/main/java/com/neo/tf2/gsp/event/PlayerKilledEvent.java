package com.neo.tf2.gsp.event;

import org.json.JSONObject;

public class PlayerKilledEvent implements Event {

    private final String attackerId;
    private final String victimId;
    private final String damageType;


    public PlayerKilledEvent(String attackerId, String victimId, String damageType) {
        this.attackerId = attackerId;
        this.victimId = victimId;
        this.damageType = damageType;
    }

    @Override
    public EventType type() {
        return EventType.PLAYER_KILLED;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("attacker", attackerId);
        json.put("victim", victimId);
        json.put("damageType", damageType);
        return json;
    }
}
