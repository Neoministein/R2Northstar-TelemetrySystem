package com.neo.r2.ts.persistence.searchable;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

import java.time.Instant;
import java.util.List;

@SearchableIndex(indexName = MatchResultSearchable.INDEX_NAME, indexPeriod = IndexPeriod.MONTHLY)
public class MatchResultSearchable extends AbstractSearchable implements Searchable {

    public static final String INDEX_NAME = "match-result";

    protected String matchId;
    protected String map;
    protected String gamemode;
    protected List<String> tags;
    protected Instant timestamp = Instant.now();

    protected String uId;
    protected Boolean hasWon;
    protected Integer pgsEliminated;
    protected Integer pgsKills;
    protected Integer pgsDeaths;
    protected Integer pgsPilotKills;
    protected Integer pgsTitanKills;
    protected Integer pgsNpcKills;
    protected Integer pgsAssists;
    protected Integer pgsScore;
    protected Integer pgsAssaultScore;
    protected Integer pgsDefenseScore;
    protected Integer pgsDistanceScore;
    protected Integer pgsDetonationScore;

    public MatchResultSearchable(JsonNode event, MatchStateWrapper matchStateWrapper) {
        this.matchId = matchStateWrapper.getMatchId();
        this.map = matchStateWrapper.getMap();
        this.gamemode = matchStateWrapper.getGameMode();
        this.tags = matchStateWrapper.getTags();

        this.uId = event.get("entityId").asText();
        JsonUtil.ifPresent(event.get("hasWon") , v -> hasWon = v.asBoolean());

        JsonUtil.ifPresent(event.get("PGS_ELIMINATED") ,        v -> pgsEliminated = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_KILLS") ,             v -> pgsKills = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_DEATHS") ,            v -> pgsDeaths = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_PILOT_KILLS") ,       v -> pgsPilotKills = v.asInt());


        JsonUtil.ifPresent(event.get("PGS_TITAN_KILLS") ,       v -> pgsTitanKills = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_NPC_KILLS") ,         v -> pgsNpcKills = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_ASSISTS") ,           v -> pgsAssists = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_SCORE") ,             v -> pgsScore = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_ASSAULT_SCORE") ,     v -> pgsAssaultScore = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_DEFENSE_SCORE") ,     v -> pgsDefenseScore = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_DISTANCE_SCORE") ,    v -> pgsDistanceScore = v.asInt());
        JsonUtil.ifPresent(event.get("PGS_DETONATION_SCORE") ,  v -> pgsDetonationScore = v.asInt());
    }

    protected MatchResultSearchable() {

    }
}
