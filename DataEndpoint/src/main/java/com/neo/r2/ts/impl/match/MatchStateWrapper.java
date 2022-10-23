package com.neo.r2.ts.impl.match;

import com.fasterxml.jackson.databind.JsonNode;

public class MatchStateWrapper {

    private final JsonNode matchState;

    public MatchStateWrapper(JsonNode matchState) {
        this.matchState = matchState;
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
}
