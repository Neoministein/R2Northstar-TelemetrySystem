package com.neo.r2.ts.impl.match.event.processor.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.common.impl.json.JsonUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EntityKilledEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "EntityKilled";
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        Optional<ObjectNode> victimPlayer = matchStateToUpdate.getPlayer(event.get("victimId").asText());
        victimPlayer.ifPresent(jsonNodes -> jsonNodes.put("isAlive", false));
    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        if (!saveSearchable()) {
            return List.of();
        }

        ObjectNode data = JsonUtil.emptyObjectNode();
        String victimId = event.get("victimId").asText();
        String attackerId = event.get("attackerId").asText();
        ObjectNode victim = endMatchState.getEntity(victimId).orElse(null);

        data.set("victim", victim);
        data.put("damageType", event.get("damageType").asText());

        MatchEventSearchable searchable = new MatchEventSearchable(endMatchState, getEventName(),
                endMatchState.getEntity(attackerId).orElse(null));
        searchable.setData(data);
        return List.of(searchable);
    }
}
