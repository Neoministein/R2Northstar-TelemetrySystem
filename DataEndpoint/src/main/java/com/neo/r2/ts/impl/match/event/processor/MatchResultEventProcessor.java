package com.neo.r2.ts.impl.match.event.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.impl.result.MatchResultService;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.r2.ts.persistence.searchable.MatchResultSearchable;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class MatchResultEventProcessor extends AbstractBasicEventProcessor {

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    protected MatchResultService matchResultService;

    @Override
    public void handleIncomingEvent(String matchId, JsonNode event, MatchStateWrapper matchStateWrapper) {
        searchProvider.index(new MatchResultSearchable(event, matchStateWrapper));
        matchResultService.invalidateStatsCache();
    }

    @Override
    public void updateMatchState(JsonNode event, MatchStateWrapper matchStateToUpdate) {

    }

    @Override
    public List<MatchEventSearchable> parseToSearchable(JsonNode event, MatchStateWrapper endMatchState) {
        return List.of();
    }

    @Override
    public void cleanUpState(JsonNode event, MatchStateWrapper matchStateToUpdate) {}

    @Override
    public boolean shouldBeAddedToMatchState() {
        return false;
    }

    @Override
    public String getEventName() {
        return "MatchResult";
    }

    @Override
    protected String getSchemaName() {
        return "state/MatchResult.json";
    }
}
