package com.neo.r2.ts.impl.match.event.processor.entity;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
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
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        String entityId = event.get("entityId").asText();
        matchStateToUpdate.getEntity(entityId).ifPresent(e -> e.set("team", event.get("team")));
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState) {
        return List.of();
    }
}
