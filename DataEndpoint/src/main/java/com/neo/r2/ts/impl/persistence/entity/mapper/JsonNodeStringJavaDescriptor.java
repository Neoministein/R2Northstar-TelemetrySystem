package com.neo.r2.ts.impl.persistence.entity.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.util.common.impl.exception.InternalJsonException;
import com.neo.util.common.impl.json.JsonUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
@Converter
public class JsonNodeStringJavaDescriptor implements AttributeConverter<JsonNode, String> {
    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return JsonUtil.toJson(jsonNode);
        } catch (InternalJsonException e) {
            throw new IllegalArgumentException("The given JsonNode object value: " + jsonNode + " cannot be transformed to a String", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String string) {
        try {
            return JsonUtil.fromJson(string);
        } catch (InternalJsonException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to JsonNode object", e);
        }
    }
}