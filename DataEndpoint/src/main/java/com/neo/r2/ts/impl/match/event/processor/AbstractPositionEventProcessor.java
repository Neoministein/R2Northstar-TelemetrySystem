package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.EntityStateWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;

import java.util.Optional;

public abstract class AbstractPositionEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    protected final SearchProvider searchProvider;

    protected AbstractPositionEventProcessor(SearchProvider searchProvider, JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
        this.searchProvider = searchProvider;
    }

    protected abstract Optional<? extends EntityStateWrapper> getEntity(MatchStateWrapper matchStateToUpdate, String entityId);

    @Override
    protected String getSchemaName() {
        return "state/EntityPositionUpdate.json";
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        JsonNode entities = event.get("positions");
        for (int i = 0; i < entities.size(); i++) {
            JsonNode newEntityData = entities.get(i);
            Optional<? extends EntityStateWrapper> entityOpt = getEntity(matchState, newEntityData.get("entityId").asText());

            if (entityOpt.isPresent()) {
                EntityStateWrapper entity = entityOpt.get();
                entity.setDistance(calculateDistance(entity.getPosition(), newEntityData.get("position")));

                entity.setPosition(newEntityData.get("position"));
                entity.setRotation(newEntityData.get("rotation"));
                entity.setVelocity(newEntityData.get("velocity"));

                entity.setHealth(newEntityData.get("health").asInt());

                saveSearchable(new MatchEventSearchable(matchState, getEventName(), entity.getRawData()));
            }
        }
    }

    protected long calculateDistance(JsonNode oldPosition, JsonNode newPosition) {
        long x = oldPosition.get("x").asLong();
        long y = oldPosition.get("y").asLong();
        long z = oldPosition.get("z").asLong();

        //Skip because of first position is default spawn position
        if (x == 0 && y == 0 && z == 0) {
            return 0;
        }


        x = Math.abs(x - newPosition.get("x").asLong());
        y = Math.abs(y - newPosition.get("y").asLong());
        z = Math.abs(z - newPosition.get("z").asLong());

        return Math.round(Math.sqrt((x * x + y * y + z * z)));
    }
}
