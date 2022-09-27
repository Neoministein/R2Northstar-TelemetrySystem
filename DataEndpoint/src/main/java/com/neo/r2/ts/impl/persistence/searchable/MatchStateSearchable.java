package com.neo.r2.ts.impl.persistence.searchable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.framework.api.persistence.search.GenericSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.Searchable;

public class MatchStateSearchable extends GenericSearchable implements Searchable {

    public static final String F_ENTITY_ID = "entityId";

    public static final String F_PLAYERS = "players";
    public static final String F_MATCH = "matchId";
    public static final String F_MAP = "map";
    public static final String F_TIME_PASSED = "timePassed";
    public static final String F_NPC = "npcs";

    public static final String F_EVENTS = "events";

    public static final String F_IS_VICTIM_PLAYER = "isVictimPlayer";
    public static final String F_IS_ATTACKER_PLAYER = "isAttackerPlayer";
    public static final String F_VICTIM = "victimId";
    public static final String F_ATTACKER = "attackerId";
    public static final String F_DAMAGE_TYPE = "damageType";

    public static final String F_PLAYER_ID = "playerId";
    public static final String F_NCP_ID = "npcId";

    public MatchStateSearchable(ObjectNode objectNode) {
        setJsonNode(objectNode);
        setBusinessId(objectNode.get(F_MATCH).asText() + ":" + objectNode.get(F_TIME_PASSED).asInt());
    }

    @Override
    public String getClassName() {
        return MatchStateSearchable.class.getSimpleName();
    }

    @Override
    public IndexPeriod getIndexPeriod() {
        return IndexPeriod.MONTHLY;
    }

    @Override
    public String getIndexName() {
        return "match-state";
    }
}
