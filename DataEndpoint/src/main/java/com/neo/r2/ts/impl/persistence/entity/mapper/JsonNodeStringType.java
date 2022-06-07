package com.neo.r2.ts.impl.persistence.entity.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class JsonNodeStringType extends AbstractSingleColumnStandardBasicType<JsonNode> implements
        DiscriminatorType<JsonNode> {

    public static final JsonNodeStringType INSTANCE = new JsonNodeStringType();

    public JsonNodeStringType() {
        super(VarcharTypeDescriptor.INSTANCE, JsonNodeStringJavaDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "jsonnode";
    }

    @Override
    public JsonNode stringToObject(String xml) {
        return fromString(xml);
    }

    @Override
    public String objectToSQLString(JsonNode value, Dialect dialect) {
        return '\'' + toString(value) + '\'';
    }
}