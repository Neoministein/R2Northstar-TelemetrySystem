package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public class MatchStateWrapper {

    private final JsonNode matchState;
    private final Instant timeStamp;

    public MatchStateWrapper(JsonNode matchState) {
        this.matchState = matchState;
        this.timeStamp = Instant.now();
    }

    public String getMatchId() {
        return matchState.get("matchId").asText();
    }

    public int getNumberOfPlayers() {
        return matchState.get("players").size();
    }

    public JsonNode getJson() {
        return matchState;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }
}
