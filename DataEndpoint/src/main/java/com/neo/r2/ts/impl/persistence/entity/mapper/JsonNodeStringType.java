package com.neo.r2.ts.impl.persistence.entity.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.usertype.UserTypeSupport;

import java.sql.Types;

public class JsonNodeStringType extends UserTypeSupport<JsonNode> {
    public JsonNodeStringType() {
        super(JsonNode.class, Types.VARCHAR);
    }
}
