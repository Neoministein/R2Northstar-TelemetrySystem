package com.neo.r2.ts.persistence.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

import java.time.Instant;

@SearchableIndex(indexName = "match-event", indexPeriod = IndexPeriod.WEEKLY)
public class MatchEventSearchable extends AbstractSearchable implements Searchable {

    public static final String EVENT_TYPE = "eventType";

    private String matchId;
    private String map;
    private int timePassed;
    private JsonNode entity;

    private String eventType;
    private JsonNode data;
    private Instant timestamp = Instant.now();

    public MatchEventSearchable(MatchStateWrapper matchState, String eventType, JsonNode entity) {
        this.matchId = matchState.getMatchId();
        this.map = matchState.getMap();
        this.timePassed = matchState.getTimePassed();
        this.eventType = eventType;
        this.entity = entity;
    }

    protected MatchEventSearchable() {

    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(int timePassed) {
        this.timePassed = timePassed;
    }

    public JsonNode getEntity() {
        return entity;
    }

    public void setEntity(JsonNode entity) {
        this.entity = entity;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
