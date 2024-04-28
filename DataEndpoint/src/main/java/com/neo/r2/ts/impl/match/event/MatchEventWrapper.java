package com.neo.r2.ts.impl.match.event;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wraps the incoming raw match event data.
 * <p>
 * It is expected that the raw data adheres to the schema: BasicEvent.json
 */
public class MatchEventWrapper {

    protected final String matchId;
    protected final String entityId;
    protected final String eventType;
    protected final JsonNode rawData;

    public MatchEventWrapper(String matchId, JsonNode rawData) {
        this.matchId = matchId;
        this.rawData = rawData;
        this.eventType = rawData.get("eventType").asText();
        this.entityId = rawData.get("entityId").asText();
    }

    public String getMatchId() {
        return matchId;
    }

    public String getEntityId() {
        return entityId;
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
