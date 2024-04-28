package com.neo.r2.ts.impl.match.event.processor.entity;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.Searchable;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EntityChangeTeamEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public EntityChangeTeamEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/EntityChangedTeam.json";
    }

    @Override
    public String getEventName() {
        return "EntityChangedTeam";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchStateToUpdate) {
        matchStateToUpdate.getEntity(event.getEntityId()).ifPresent(e -> e.getRawData().set("team", event.get("team")));
    }

    @Override
    public List<Searchable> parseToSearchable(MatchEventWrapper event, MatchStateWrapper endMatchState) {
        return List.of();
    }
}
