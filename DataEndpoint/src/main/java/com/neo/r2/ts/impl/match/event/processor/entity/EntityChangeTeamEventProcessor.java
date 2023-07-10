package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EntityChangeTeamEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/EntityChangedTeam.json";
    }

    @Override
    public String getEventName() {
        return "EntityChangedTeam";
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        String entityId = event.get("entityId").asText();
        matchStateToUpdate.getEntity(entityId).ifPresent(e -> e.set("team", event.get("team")));
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        return List.of();
    }
}
