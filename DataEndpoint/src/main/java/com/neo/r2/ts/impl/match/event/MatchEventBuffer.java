package com.neo.r2.ts.impl.match.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.util.framework.api.cache.Cache;
import com.neo.util.framework.api.cache.spi.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatchEventBuffer {

    @Inject
    @CacheName("matchEventBuffer")
    protected Cache matchEventBuffer;

    public void addToBuffer(String matchId, JsonNode event) {
        Optional<List<JsonNode>> events = matchEventBuffer.get(matchId);
        if (events.isPresent()) {
            events.get().add(event);
        } else {
            List<JsonNode> newEventList = new LinkedList<>();
            newEventList.add(event);
            matchEventBuffer.put(matchId, newEventList);
        }
    }

    public List<JsonNode> getBuffer(String matchId) {
        Optional<List<JsonNode>> events = matchEventBuffer.get(matchId);
        matchEventBuffer.invalidate(matchId);
        return events.orElse(List.of());
    }
}
