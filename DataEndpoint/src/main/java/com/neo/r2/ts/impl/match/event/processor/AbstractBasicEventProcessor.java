package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.Config;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.networknt.schema.JsonSchema;
import jakarta.inject.Inject;

import java.util.List;

public abstract class AbstractBasicEventProcessor implements MatchEventProcessor {

    protected JsonSchema jsonSchema;
    protected boolean enabled;
    protected int modulo;

    protected int moduloCount = 0;

    protected abstract String getSchemaName();

    @Inject
    public void init(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        this.jsonSchema = jsonSchemaLoader.getJsonSchema(getSchemaName()).orElseThrow();
        Config config = configService.get("r2ts").get("match").get("event").get(getEventName());
        enabled = config.get("enabled").asBoolean().orElse(true);
        modulo = config.get("modulo").asInt().orElse(1);
    }

    protected boolean saveSearchable() {
        return enabled && moduloCount++ % modulo == 0;
    }

    @Override
    public void handleIncomingEvent(String matchId, JsonNode event, MatchStateWrapper matchStateWrapper) {}

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.addEvent(getEventName(), event);
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        if (!saveSearchable()) {
            return List.of();
        }

        String entityId = event.get("entityId").asText();
        return List.of(new MatchEventSearchable(endMatchState, getEventName(),
                endMatchState.getEntity(entityId).orElse(parseBackupEntity(entityId))));
    }

    @Override
    public void cleanUpState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        if (shouldBeAddedToMatchState()) {
            matchStateToUpdate.clearEvents(getEventName());
        }
    }

    protected ObjectNode parseBackupEntity(String entityId) {
        return JsonUtil.emptyObjectNode().put("entityId", entityId);
    }

    @Override
    public JsonSchema getSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(JsonSchema jsonSchema) {
        this.jsonSchema = jsonSchema;
    }
}
