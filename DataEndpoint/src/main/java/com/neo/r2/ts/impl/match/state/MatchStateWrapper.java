package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

public class MatchStateWrapper {

    private final JsonNode matchState;
    private final Date timeStamp;

    public MatchStateWrapper(JsonNode matchState) {
        this.matchState = matchState;
        this.timeStamp = new Date();
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

    public Date getTimeStamp() {
        return timeStamp;
    }
}
