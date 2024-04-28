package com.neo.r2.ts.impl.match.event.processor;

import com.neo.r2.ts.api.match.event.MatchEventProcessor;
import com.neo.r2.ts.impl.match.event.MatchEventWrapper;
import com.neo.r2.ts.impl.match.state.MatchStateWrapper;
import com.neo.r2.ts.persistence.searchable.MatchStateSearchable;
import com.neo.r2.ts.web.socket.MatchStateOutputSocket;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.api.config.ConfigService;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StateEndEventProcessor extends AbstractBasicEventProcessor implements MatchEventProcessor {

    public static final String STATE_END_EVENT = "StateEnd";

    protected final MatchStateOutputSocket matchStateOutputSocket;

    @Inject
    protected StateEndEventProcessor(SearchProvider searchProvider, MatchStateOutputSocket matchStateOutputSocket,
                                     JsonSchemaLoader jsonSchemaLoader, ConfigService configService) {
        super(searchProvider, jsonSchemaLoader, configService);
        this.matchStateOutputSocket = matchStateOutputSocket;
    }

    @Override
    public void processEvent(MatchEventWrapper event, MatchStateWrapper matchState) {
        matchState.updateTimeStamp(event.get("timePassed").asInt());


        matchStateOutputSocket.broadcast(matchState.getMatchId(), JsonUtil.toJson(matchState.getState()));
        searchProvider.index(new MatchStateSearchable(matchState.getState().deepCopy()));

        matchState.clearEvents();
    }

    @Override
    public String getEventName() {
        return STATE_END_EVENT;
    }

    @Override
    protected String getSchemaName() {
        return "state/StateEndEvent.json";
    }
}
