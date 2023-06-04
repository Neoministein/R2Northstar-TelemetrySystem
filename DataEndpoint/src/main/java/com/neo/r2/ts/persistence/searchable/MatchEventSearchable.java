package com.neo.r2.ts.persistence.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

@SearchableIndex(indexName = "match-event", indexPeriod = IndexPeriod.WEEKLY)
public class MatchEventSearchable extends AbstractSearchable implements Searchable {

    public static final String F_MATCH_ID = "matchId";

    public static final String F_VICTIM = "victim";

    private String matchId;
    private String map;
    private int timePassed;
    private JsonNode entity;

    private String eventType;
    private JsonNode data;

    public MatchEventSearchable(JsonNode matchState, MatchEvent eventType) {
        this.matchId = matchState.get(MatchStateSearchable.F_MATCH).asText();
        this.map = matchState.get(MatchStateSearchable.F_MAP).asText();
        this.timePassed = matchState.get(MatchStateSearchable.F_TIME_PASSED).asInt();
        this.eventType = eventType.fieldName;
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
}
