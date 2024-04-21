package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractPositionEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    protected AbstractPositionEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    protected abstract Optional<ObjectNode> getEntity(MatchStateWrapper matchStateToUpdate, String entityId);

    protected abstract Collection<ObjectNode> getAllEntities(MatchStateWrapper matchStateWrapper);

    @Override
    protected String getSchemaName() {
        return "state/EntityPositionUpdate.json";
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        JsonNode entities = event.get("positions");
        for (int i = 0; i < entities.size(); i++) {
            JsonNode newEntityData = entities.get(i);
            Optional<ObjectNode> entityOpt = getEntity(matchStateToUpdate, newEntityData.get("entityId").asText());

            if (entityOpt.isPresent()) {
                ObjectNode entity = entityOpt.get();
                entity.put("distance", calculateDistance(entity.get("position"), newEntityData.get("position")));
                entity.set("position",newEntityData.get("position"));
                entity.set("rotation",newEntityData.get("rotation"));
                entity.set("velocity",newEntityData.get("velocity"));

                entity.set("health", newEntityData.get("health"));
            }
        }
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(MatchEvent event, MatchStateWrapper endMatchState) {
        List<MatchEventSearchable> matchEventSearchableList = new ArrayList<>(endMatchState.getNumberOfPlayers() + 1);
        for (JsonNode player: getAllEntities(endMatchState)) {
            if (saveSearchable()) {
                matchEventSearchableList.add(new MatchEventSearchable(endMatchState, getEventName(), player));
            }
        }
        return matchEventSearchableList;
    }

    protected long calculateDistance(JsonNode oldPosition, JsonNode newPosition) {
        long x = oldPosition.get("x").asLong();
        long y = oldPosition.get("y").asLong();
        long z = oldPosition.get("z").asLong();

        //Skip because of first position is default spawn position
        if (x == 0 && y == 0 && z == 0) {
            return 0;
        }

        x -= newPosition.get("x").asLong();
        y -= newPosition.get("y").asLong();
        z -= newPosition.get("z").asLong();

        return Math.round(Math.sqrt((x * x + y * y + z * z)));
    }
}
