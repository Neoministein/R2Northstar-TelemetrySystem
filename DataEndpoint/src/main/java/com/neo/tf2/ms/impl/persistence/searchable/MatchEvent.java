package com.neo.tf2.ms.impl.persistence.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.impl.json.JsonUtil;
import com.neo.javax.api.persitence.search.GenericSearchable;
import com.neo.javax.api.persitence.search.IndexPeriod;
import com.neo.javax.api.persitence.search.Searchable;

public class MatchEvent extends GenericSearchable implements Searchable {

    public static final String F_MATCH_ID = "matchId";

    public static final String F_IS_PLAYER = "isPlayer";

    public static final String F_VICTIM = "victim";
    public static final String F_DAMAGE_TYPE = "damageType";

    public static final String T_POSTION = "postion";

    private String matchId;
    private String map;
    private int timePassed;
    private JsonNode entity;

    private String eventType;
    private JsonNode data;

    public MatchEvent(JsonNode matchState, String eventType) {
        this.matchId = matchState.get(MatchState.F_MATCH).asText();
        this.map = matchState.get(MatchState.F_MAP).asText();
        this.timePassed = matchState.get(MatchState.F_TIME_PASSED).asInt();
        this.eventType = eventType;
    }

    @Override
    public ObjectNode getJsonNode() {
        return JsonUtil.toObjectNode(this);
    }

    @Override
    public String getClassName() {
        return MatchState.class.getSimpleName();
    }

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.MONTHLY;
    }

    @Override
    public String getIndexName() {
        return "match-event";
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