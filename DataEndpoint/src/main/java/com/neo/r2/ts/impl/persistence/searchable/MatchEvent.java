package com.neo.r2.ts.impl.persistence.searchable;

public enum MatchEvent {

    /**
     * Incoming events from R2
     */
    CONNECT("playerConnect"),
    DISCONNECT("playerDisconnect"),
    PLAYER_RESPAWNED("playerRespawned"),
    PILOT_BECOMES_TITAN("pilotBecomesTitan"),
    TITAN_BECOMES_PILOT("titanBecomesPilot"),
    JUMP("playerJump"),
    DOUBLE_JUMP("playerDoubleJump"),
    MANTLE("playerMantle"),
    NEW_LOADOUT("playerGetsNewPilotLoadout"),
    ENTITY_KILLED("entityKilled"),
    NPC_LEECHED("npcLeeched"),

    /**
     * Events parsed from Match state
     */
    PLAYER_POSITION("playerPosition"),
    NPC_POSITION("npcPositions");

    public final String fieldName;

    MatchEvent(String fieldName) {
        this.fieldName = fieldName;
    }
}
