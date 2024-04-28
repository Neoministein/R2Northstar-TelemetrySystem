package com.neo.r2.ts.impl.match.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.util.common.impl.json.JsonUtil;

public class EntityStateWrapper {

    protected final String entityId;
    protected final ObjectNode rawData;

    public EntityStateWrapper(String entityType, JsonNode creationEvent) {
        rawData = JsonUtil.emptyObjectNode();
        entityId = creationEvent.get("entityId").asText();

        rawData.put("entityId", entityId);
        rawData.put("team", creationEvent.get("team").asInt());
        rawData.put("entityType", entityType);
        rawData.set("titanClass", creationEvent.get("titanClass"));
        rawData.put("health", 100);
        rawData.put("distance", 0);

        ObjectNode position = rawData.withObject("/position");
        position.put("x", 0);
        position.put("y", 0);
        position.put("z", 0);

        ObjectNode rotation = rawData.withObject("/rotation");
        rotation.put("x", 0);
        rotation.put("y", 0);
        rotation.put("z", 0);

        ObjectNode velocity = rawData.withObject("/velocity");
        velocity.put("x", 0);
        velocity.put("y", 0);
        velocity.put("z", 0);

        ObjectNode equipment = rawData.withObject("/equipment");
        equipment.put("primary", CustomConstants.UNKNOWN);
        equipment.put("secondary", CustomConstants.UNKNOWN);
    }

    public String getEntityId() {
        return entityId;
    }

    public ObjectNode getRawData() {
        return rawData;
    }

    public void setHealth(int health) {
        rawData.put("health", health);
    }

    public void setDistance(long distance) {
        rawData.put("distance", distance);
    }

    public void setTitanClass(String titanClass) {
        rawData.put("titanClass", titanClass);
    }

    public JsonNode getPosition() {
        return rawData.get("position");
    }

    public void setPosition(JsonNode position) {
        rawData.set("position", position);
    }

    public void setRotation(JsonNode rotation) {
        rawData.set("rotation", rotation);
    }

    public void setVelocity(JsonNode velocity) {
        rawData.set("velocity", velocity);
    }

    public void setPrimary(String primary) {
        rawData.withObject("/equipment").put("primary", primary);
    }

    public void setSecondary(String secondary) {
        rawData.withObject("/equipment").put("secondary", secondary);
    }
}
