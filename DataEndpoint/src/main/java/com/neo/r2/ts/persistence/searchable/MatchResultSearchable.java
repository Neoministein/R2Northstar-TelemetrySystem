package com.neo.r2.ts.persistence.searchable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.web.rest.result.MatchResultDto;
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

    public static final String MATCH_ID = "matchId";
    public static final String MAP = "map";
    public static final String GAMEMODE = "gamemode";
    public static final String TIME_STAMP = "timestamp";

    public static final String PGS_ELIMINATED = "PGS_ELIMINATED";
    public static final String PGS_KILLS = "PGS_KILLS";
    public static final String PGS_DEATHS = "PGS_DEATHS";
    public static final String PGS_PILOT_KILLS = "PGS_PILOT_KILLS";
    public static final String PGS_TITAN_KILLS = "PGS_TITAN_KILLS";
    public static final String PGS_NPC_KILLS = "PGS_NPC_KILLS";
    public static final String PGS_ASSISTS = "PGS_ASSISTS";
    public static final String PGS_SCORE = "PGS_SCORE";
    public static final String PGS_ASSAULT_SCORE = "PGS_ASSAULT_SCORE";
    public static final String PGS_DEFENSE_SCORE = "PGS_DEFENSE_SCORE";
    public static final String PGS_DISTANCE_SCORE = "PGS_DISTANCE_SCORE";
    public static final String PGS_DETONATION_SCORE = "PGS_DETONATION_SCORE";

    protected String matchId;
    protected String map;
    protected String gamemode;
    protected List<String> tags;
    protected Instant timestamp = Instant.now();

    protected String uId;
    protected Boolean hasWon;
    @JsonProperty(value = PGS_ELIMINATED)
    protected Integer pgsEliminated;
    @JsonProperty(value = PGS_KILLS)
    protected Integer pgsKills;
    @JsonProperty(value = PGS_DEATHS)
    protected Integer pgsDeaths;
    @JsonProperty(value = PGS_PILOT_KILLS)
    protected Integer pgsPilotKills;
    @JsonProperty(value = PGS_TITAN_KILLS)
    protected Integer pgsTitanKills;
    @JsonProperty(value = PGS_NPC_KILLS)
    protected Integer pgsNpcKills;
    @JsonProperty(value = PGS_ASSISTS)
    protected Integer pgsAssists;
    @JsonProperty(value = PGS_SCORE)
    protected Integer pgsScore;
    @JsonProperty(value = PGS_ASSAULT_SCORE)
    protected Integer pgsAssaultScore;
    @JsonProperty(value = PGS_DEFENSE_SCORE)
    protected Integer pgsDefenseScore;
    @JsonProperty(value = PGS_DISTANCE_SCORE)
    protected Integer pgsDistanceScore;
    @JsonProperty(value = PGS_DETONATION_SCORE)
    protected Integer pgsDetonationScore;

    public MatchResultSearchable(MatchResultDto matchResultDto, MatchResultDto.Player player) {
        this.matchId = matchResultDto.matchId().orElse(null);
        this.map = matchResultDto.map();
        this.gamemode = matchResultDto.gamemode();
        matchResultDto.tags().ifPresent(tags -> this.tags = List.of(tags.replaceAll("\\s+", "").split(",")));

        this.uId = player.uId();
        this.hasWon = player.hasWon();

        player.pgsEliminated().ifPresent(v -> this.pgsEliminated = v);
        player.pgsKills().ifPresent(v -> this.pgsKills = v);
        player.pgsDeaths().ifPresent(v -> this.pgsDeaths = v);
        player.pgsPilotKills().ifPresent(v -> this.pgsPilotKills = v);
        player.pgsTitanKills().ifPresent(v -> this.pgsTitanKills = v);
        player.pgsNpcKills().ifPresent(v -> this.pgsNpcKills = v);
        player.pgsAssists().ifPresent(v -> this.pgsAssists = v);
        player.pgsScore().ifPresent(v -> this.pgsScore = v);
        player.pgsAssaultScore().ifPresent(v -> this.pgsAssaultScore = v);
        player.pgsDefenseScore().ifPresent(v -> this.pgsDefenseScore = v);
        player.pgsDistanceScore().ifPresent(v -> this.pgsDistanceScore = v);
        player.pgsDetonationScore().ifPresent(v -> this.pgsDetonationScore = v);
    }

    public MatchResultSearchable(MatchEventWrapper event, MatchStateWrapper matchStateWrapper) {
        this.matchId = matchStateWrapper.getMatchId();
        this.map = matchStateWrapper.getMap();
        this.gamemode = matchStateWrapper.getGameMode();
        this.tags = matchStateWrapper.getTags();

        this.uId = event.get("entityId").asText();
        JsonUtil.ifPresent(event.get("hasWon") , v -> hasWon = v.asBoolean());

        JsonUtil.ifPresent(event.get(PGS_ELIMINATED) ,        v -> pgsEliminated = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_KILLS) ,             v -> pgsKills = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_DEATHS) ,            v -> pgsDeaths = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_PILOT_KILLS) ,       v -> pgsPilotKills = v.asInt());


        JsonUtil.ifPresent(event.get(PGS_TITAN_KILLS) ,       v -> pgsTitanKills = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_NPC_KILLS) ,         v -> pgsNpcKills = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_ASSISTS) ,           v -> pgsAssists = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_SCORE) ,             v -> pgsScore = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_ASSAULT_SCORE) ,     v -> pgsAssaultScore = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_DEFENSE_SCORE) ,     v -> pgsDefenseScore = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_DISTANCE_SCORE) ,    v -> pgsDistanceScore = v.asInt());
        JsonUtil.ifPresent(event.get(PGS_DETONATION_SCORE) ,  v -> pgsDetonationScore = v.asInt());
    }

    protected MatchResultSearchable() {

    }

    public MatchResultSearchable addMatchId(String matchId) {
        this.matchId = matchId;
        return this;
    }
}
