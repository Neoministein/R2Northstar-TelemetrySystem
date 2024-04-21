package com.neo.r2.ts.impl.match.event.processor.npc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.processor.AbstractBasicEventProcessor;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NpcSpawnEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    @Inject
    public NpcSpawnEventProcessor(JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(jsonSchemaLoader, configService);
    }

    @Override
    protected String getSchemaName() {
        return "state/" + getEventName() + ".json";
    }

    @Override
    public String getEventName() {
        return "NpcSpawn";
    }

    @Override
    public void updateMatchState(MatchEvent event, MatchStateWrapper matchStateToUpdate) {
        super.updateMatchState(event, matchStateToUpdate);
        ObjectNode npc = JsonUtil.emptyObjectNode();
        String entityId = event.get("entityId").asText();

        npc.put("entityId", entityId);
        npc.put("team", event.get("team").asInt());
        npc.set("entityType", event.get("entityType"));
        npc.set("titanClass", event.get("titanClass"));
        npc.put("health", 100);
        npc.put("distance", 0);

        ObjectNode position = npc.withObject("/position");
        position.put("x", 0);
        position.put("y", 0);
        position.put("z", 0);

        ObjectNode rotation = npc.withObject("/rotation");
        rotation.put("x", 0);
        rotation.put("y", 0);
        rotation.put("z", 0);

        ObjectNode velocity = npc.withObject("/velocity");
        velocity.put("x", 0);
        velocity.put("y", 0);
        velocity.put("z", 0);

        ObjectNode equipment = npc.withObject("/equipment");
        equipment.put("primary", CustomConstants.UNKNOWN);
        equipment.put("secondary", CustomConstants.UNKNOWN);

        matchStateToUpdate.addNpc(entityId, npc);
    }
}
