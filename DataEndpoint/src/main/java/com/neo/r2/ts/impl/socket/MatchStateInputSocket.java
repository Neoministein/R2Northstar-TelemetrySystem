package com.neo.r2.ts.impl.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.MatchStateService;
import com.neo.r2.ts.impl.security.BasicWebsocketAuthentication;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.networknt.schema.JsonSchema;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.elasticsearch.common.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@ServerEndpoint(value = MatchStateInputSocket.WS_LOCATION, configurator = BasicWebsocketAuthentication.class)
public class MatchStateInputSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateInputSocket.class);

    public static final String WS_LOCATION = "/ws/state/input";

    protected JsonSchema matchStateSchema;

    protected List<Session> sessions = new ArrayList<>();

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected JsonSchemaLoader schemaLoader;

    @PostConstruct
    public void init() {
        matchStateSchema = schemaLoader.getUnmodifiableMap().get("MatchState.json");
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JsonNode matchState = JsonUtil.fromJson(message);
            JsonSchemaUtil.isValidOrThrow(matchState, matchStateSchema);
            matchStateService.updateGameState(matchState);
        } catch (Exception ex) {
            LOGGER.warn("Unable to process match state input", ex);
        }
    }
}
