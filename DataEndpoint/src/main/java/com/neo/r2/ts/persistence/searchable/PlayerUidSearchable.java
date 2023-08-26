package com.neo.r2.ts.persistence.searchable;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

import java.time.Instant;

@SearchableIndex(indexName = PlayerUidSearchable.INDEX_NAME, indexPeriod = IndexPeriod.ALL)
public class PlayerUidSearchable extends AbstractSearchable implements Searchable {

    public static final String INDEX_NAME = "player-uid";
    public static final String U_ID = "uId";
    public static final String PLAYER_NAME = "playerName";
    public static final String LAST_UPDATE = "lastUpdate";

    protected Instant lastUpdate = Instant.now();

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
}
