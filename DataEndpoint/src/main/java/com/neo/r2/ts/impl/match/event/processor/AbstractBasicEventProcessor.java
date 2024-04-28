package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.Config;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.networknt.schema.JsonSchema;

import java.util.Optional;

public abstract class AbstractBasicEventProcessor implements MatchEventProcessor {

    protected final SearchProvider searchProvider;
    protected final JsonSchema jsonSchema;
    protected boolean enabled;
    protected int modulo;

    protected int moduloCount = 0;

    protected abstract String getSchemaName();

    protected AbstractBasicEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        this.searchProvider = searchProvider;
        this.jsonSchema = jsonSchemaLoader.requestJsonSchema(getSchemaName());
        Config config = configService.get("r2ts").get("match").get("event").get(getEventName());
        enabled = config.get("enabled").asBoolean().orElse(true);
        modulo = config.get("modulo").asInt().orElse(1);
    }

    protected void saveSearchable(Searchable searchable) {
        if (enabled && moduloCount++ % modulo == 0) {
            searchProvider.index(searchable);
        }
    }

    protected Searchable createBasicSearchable(MatchEventWrapper event, MatchStateWrapper matchState) {
        Optional<EntityStateWrapper> entityState = matchState.getEntity(event.getEntityId());

        if (entityState.isPresent()) {
            return new MatchEventSearchable(matchState, getEventName(), entityState.get().getRawData());
        }

        return new MatchEventSearchable(matchState, getEventName(), JsonUtil.emptyObjectNode().put("entityId", event.getEntityId()));
    }

    @Override
    public JsonSchema getSchema() {
        return jsonSchema;
    }
}
