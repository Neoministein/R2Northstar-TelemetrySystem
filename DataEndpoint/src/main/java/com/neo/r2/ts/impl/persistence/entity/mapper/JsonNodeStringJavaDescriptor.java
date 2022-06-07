package com.neo.r2.ts.impl.persistence.entity.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.common.impl.exception.InternalJsonException;
import com.neo.common.impl.json.JsonUtil;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class JsonNodeStringJavaDescriptor extends AbstractTypeDescriptor<JsonNode> {

    public static final JsonNodeStringJavaDescriptor INSTANCE = new JsonNodeStringJavaDescriptor();

    public JsonNodeStringJavaDescriptor() {
        super(JsonNode.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(JsonNode value) {
        try {
            return JsonUtil.toJson(value);
        } catch (InternalJsonException e) {
            throw new IllegalArgumentException("The given JsonNode object value: " + value + " cannot be transformed to a String", e);
        }
    }

    @Override
    public JsonNode fromString(String string) {
        try {
            return JsonUtil.fromJson(string);
        } catch (InternalJsonException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to JsonNode object", e);
        }
    }

    @Override
    public <X> X unwrap(JsonNode value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> JsonNode wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return fromString(value.toString());
        }

        throw unknownWrap(value.getClass());
    }
}