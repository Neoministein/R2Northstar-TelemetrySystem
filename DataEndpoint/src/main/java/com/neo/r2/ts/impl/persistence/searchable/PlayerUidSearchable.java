package com.neo.r2.ts.impl.persistence.searchable;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import jakarta.enterprise.context.Dependent;

import java.util.Date;

@Dependent
public class PlayerUidSearchable extends AbstractSearchable implements Searchable {

    public static final String INDEX_NAME = "player-uid";

    protected Date lastUpdate = new Date();

    protected String uId;
    protected String playerName;


    public PlayerUidSearchable(String  uId, String playerName) {
        this.uId = uId;
        this.playerName = playerName;
    }

    //Required by Jackson
    protected PlayerUidSearchable() {}

    @Override
    public String getBusinessId() {
        return uId;
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.ALL;
    }
}
