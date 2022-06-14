package com.neo.r2.ts.impl.persistence.searchable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.framework.api.persitence.search.GenericSearchable;
import com.neo.util.framework.api.persitence.search.IndexPeriod;
import com.neo.util.framework.api.persitence.search.Searchable;
import com.networknt.schema.JsonSchema;

public class MatchState extends GenericSearchable implements Searchable {

    public static final JsonSchema JSON_SCHEMA = JsonSchemaUtil.generateSchemaFromResource("schemas/MatchState.json");

    public static final String F_ENTITY_ID = "entityId";

    public static final String F_PLAYERS = "players";
    public static final String F_MATCH = "matchId";
    public static final String F_MAP = "map";
    public static final String F_TIME_PASSED = "timePassed";

    public static final String F_EVENTS = "events";

    public static final String F_CONNECT = "playerConnect";

    public static final String F_DISCONNECT = "playerDisconnect";

    public static final String F_KILLED = "playerKilled";
    public static final String F_VICTIM = "victimId";
    public static final String F_ATTACKER = "attackerId";
    public static final String F_WEAPON = "weapon";

    public static final String F_RESPAWNED = "playerRespawned";
    public static final String F_PILOT_BECOMES_TITAN = "pilotBecomesTitan";
    public static final String F_TITAN_BECOMES_PILOT = "titanBecomesPilot";
    public static final String F_NEW_LOADOUT = "playerGetsNewPilotLoadout";
    public static final String F_JUMP = "playerJump";
    public static final String F_DOUBLE_JUMP = "playerDoubleJump";
    public static final String F_MANTLE = "playerMantle";

    public MatchState(ObjectNode objectNode) {
        setJsonNode(objectNode);
        setBusinessId(objectNode.get(F_MATCH).asText() + ":" + objectNode.get(F_TIME_PASSED).asInt());
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
        return "match-state";
    }
}
