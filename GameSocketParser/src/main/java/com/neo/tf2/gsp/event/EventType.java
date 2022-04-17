package com.neo.tf2.gsp.event;

public enum EventType {

    PLAYER_CONNECT("playerConnect"),
    PLAYER_DISCONNECT("playerDisconnect"),
    PLAYER_BECOMES_TITAN("playerBecomesTitan"),
    TITAN_BECOMES_PLAYER("titanBecomesPlayer"),

    PLAYER_KILLED("playerKilled"),
    PLAYER_RESPAWNED("playerRespawned");

    public final String value;

    EventType(String value) {
        this.value = value;
    }
}
