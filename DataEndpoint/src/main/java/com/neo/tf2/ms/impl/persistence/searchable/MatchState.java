package com.neo.tf2.ms.impl.persistence.searchable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.javax.api.persitence.search.GenericSearchable;
import com.neo.javax.api.persitence.search.IndexPeriod;
import com.neo.javax.api.persitence.search.Searchable;
import com.networknt.schema.JsonSchema;

public class MatchState extends GenericSearchable implements Searchable {

    public static final JsonSchema JSON_SCHEMA = JsonSchemaUtil.generateSchemaFromResource("schemas/MatchState.json");

    public static final String F_PLAYER_ID = "playerId";

    public static final String F_PLAYERS = "players";
    public static final String F_MATCH = "matchId";
    public static final String F_MAP = "map";
    public static final String F_TIME_PASSED = "timePassed";

    public static final String F_EVENTS = "events";

    public static final String F_CONNECT = "eventPlayerConnect";

    public static final String F_DISCONNECT = "eventPlayerDisconnect";

    public static final String F_KILLED = "eventPlayerKilled";
    public static final String F_VICTIM = "victimId";
    public static final String F_WEAPON = "weapon";

    public static final String F_RESPAWNED = "eventPlayerRespawned";
    public static final String F_PILOT_BECOMES_TITAN = "eventPilotBecomesTitan";
    public static final String F_TITAN_BECOMES_PILOT = "eventTitanBecomesPilot";
    public static final String F_NEW_LOADOUT = "eventPlayerGetsNewPilotLoadout";
    public static final String F_JUMP = "eventPlayerJump";
    public static final String F_DOUBLE_JUMP = "eventPlayerDoubleJump";
    public static final String F_MANTLE = "eventPlayerMantle";

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
