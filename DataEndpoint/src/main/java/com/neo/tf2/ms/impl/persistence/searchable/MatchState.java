package com.neo.tf2.ms.impl.persistence.searchable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.common.impl.json.JsonSchemaUtil;
import com.neo.javax.api.persitence.search.GenericSearchable;
import com.neo.javax.api.persitence.search.IndexPeriod;
import com.neo.javax.api.persitence.search.Searchable;
import com.networknt.schema.JsonSchema;

public class MatchState extends GenericSearchable implements Searchable {

    public static final JsonSchema JSON_SCHEMA = JsonSchemaUtil.generateSchemaFromResource("schemas/MatchState.json");

    private String businessId;

    public MatchState(ObjectNode objectNode) {
        setJsonNode(objectNode);
        businessId = objectNode.get("matchId").asText() + ":" + getCreationDate().getTime();
    }

    @Override
    public String getBusinessId() {
        return businessId;
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
