package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.util.framework.api.config.Config;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.networknt.schema.JsonSchema;

public abstract class AbstractBasicEventProcessor implements MatchEventProcessor {

    protected final SearchProvider searchProvider;
    protected final JsonSchema jsonSchema;
    protected boolean enabled;
    protected int modulo;

    protected int moduloCount;

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

    @Override
    public JsonSchema getSchema() {
        return jsonSchema;
    }
}
