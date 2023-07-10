package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractPositionEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    protected abstract Optional<ObjectNode> getEntity(MatchStateWrapper matchStateToUpdate, String entityId);

    protected abstract Collection<ObjectNode> getAllEntities(MatchStateWrapper matchStateWrapper);

    @Override
    protected String getSchemaName() {
        return "state/EntityPositionUpdate.json";
    }

    @Override
    public boolean shouldBeAddedToMatchState() {
        return false;
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        JsonNode entities = event.get("positions");
        for (int i = 0; i < entities.size(); i++) {
            JsonNode newEntityData = entities.get(i);
            Optional<ObjectNode> entityOpt = getEntity(matchStateToUpdate, newEntityData.get("entityId").asText());

            if (entityOpt.isPresent()) {
                ObjectNode entity = entityOpt.get();
                entity.set("position",newEntityData.get("position"));
                entity.set("rotation",newEntityData.get("rotation"));
                entity.set("velocity",newEntityData.get("velocity"));

                entity.set("health", newEntityData.get("health"));
            }
        }
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        List<MatchEventSearchable> matchEventSearchableList = new ArrayList<>(endMatchState.getNumberOfPlayers() + 1);
        for (JsonNode player: getAllEntities(endMatchState)) {
            matchEventSearchableList.add(new MatchEventSearchable(endMatchState, getEventName(), player));
        }
        return matchEventSearchableList;
    }
}
