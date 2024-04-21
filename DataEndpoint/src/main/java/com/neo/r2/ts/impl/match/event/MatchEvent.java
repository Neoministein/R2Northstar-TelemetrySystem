package com.neo.r2.ts.impl.match.event;

import com.fasterxml.jackson.databind.JsonNode;

public class MatchEvent {

    protected final String matchId;
    protected final String eventType;
    protected final JsonNode rawData;

    public MatchEvent(String matchId, JsonNode rawData) {
        this.matchId = matchId;
        this.rawData = rawData;
        this.eventType = rawData.get("eventType").asText();
    }

    public String getMatchId() {
        return matchId;
    }

    public String getEventType() {
        return eventType;
    }

    public JsonNode getRawData() {
        return rawData;
    }

    public JsonNode get(String fieldName) {
        return rawData.get(fieldName);
    }
}
