package com.neo.r2.ts.impl.persistence.entity.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.util.common.impl.json.JsonUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonNodeStringJavaDescriptor implements AttributeConverter<JsonNode, String> {

    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        return JsonUtil.toJson(jsonNode);
    }

    @Override
    public JsonNode convertToEntityAttribute(String string) {
        return JsonUtil.fromJson(string);
    }
}