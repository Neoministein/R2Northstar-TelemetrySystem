package com.neo.r2.ts.impl.persistence.searchable;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neo.r2.ts.impl.rest.dto.inbound.MatchResultDto;
import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import jakarta.enterprise.context.Dependent;

import java.util.Date;

@Dependent
public class MatchResultSearchable extends AbstractSearchable implements Searchable {

    public static final String INDEX_NAME = "match-result";

    protected Date timestamp = new Date();

    protected String matchId;
    protected String map;
    protected String gamemode;

    @JsonUnwrapped
    protected MatchResultDto.Player player;

    public MatchResultSearchable(MatchResultDto matchResultDto, MatchResultDto.Player player) {
        this.matchId = matchResultDto.matchId();
        this.map = matchResultDto.map();
        this.gamemode = matchResultDto.gamemode();
        this.player = player;
    }

    //Required by Jackson
    protected MatchResultSearchable() {}

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.MONTHLY;
    }

    public MatchResultSearchable addMatchId(String matchId) {
        this.matchId = matchId;
        return this;
    }
}
