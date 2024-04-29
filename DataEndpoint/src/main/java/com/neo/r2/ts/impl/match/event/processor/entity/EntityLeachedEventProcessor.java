package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EntityLeachedEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    protected EntityLeachedEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/EntityLeached.json";
    }

    @Override
    public String getEventName() {
        return "EntityLeached";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        ObjectNode entity = matchState.getEntity(event.getEntityId()).map(EntityStateWrapper::getRawData).orElse(null);
        ObjectNode leachedSpecter = matchState.getNpc(event.get("specterId").asText()).map(EntityStateWrapper::getRawData).orElse(null);

        saveSearchable(new MatchEventSearchable(matchState, getEventName(), entity, leachedSpecter));
    }
}
