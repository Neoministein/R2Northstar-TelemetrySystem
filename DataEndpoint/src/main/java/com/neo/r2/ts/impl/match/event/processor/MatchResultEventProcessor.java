package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MatchResultEventProcessor extends AbstractBasicEventProcessor {

    @Inject
    protected MatchResultEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider ,jsonSchemaLoader, configService);
    }

    @Override
    public String getEventName() {
        return "MatchResult";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        saveSearchable(new MatchResultSearchable(event, matchState));
    }

    @Override
    protected String getSchemaName() {
        return "state/MatchResult.json";
    }
}
