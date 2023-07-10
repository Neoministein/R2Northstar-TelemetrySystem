package com.neo.r2.ts.web.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.api.socket.AbstractMonitorableWebsocket;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.r2.ts.impl.match.state.MatchStateService;
import com.neo.r2.ts.impl.security.BasicWebsocketAuthentication;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.networknt.schema.JsonSchema;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@ApplicationScoped
@ServerEndpoint(value = MatchEventInputSocket.WS_LOCATION, configurator = BasicWebsocketAuthentication.class)
public class MatchEventInputSocket extends AbstractMonitorableWebsocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventInputSocket.class);

    public static final String WS_LOCATION = "/ws/state/input/{id}";

    protected JsonSchema eventSchema;

    protected List<Session> sessions = new ArrayList<>();

    @Inject
    protected MatchStateService matchStateService;

    @Inject
    protected MatchEventService matchEventService;

    @Inject
    protected JsonSchemaLoader schemaLoader;

    @PostConstruct
    public void init() {
        eventSchema = schemaLoader.getUnmodifiableMap().get("state/Event.json");
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
    public void onMessage(Session session, String message, @PathParam("id") String matchId) throws IOException {
        updateIncomingSocketLog(session, message);
        try {
            JsonNode event = JsonUtil.fromJson(message);
            JsonSchemaUtil.isValidOrThrow(event, eventSchema);
            matchEventService.delegateToEventProcessor(matchId, event);
        } catch (ValidationException ex) {
            LOGGER.warn("A validation exception occurred [{}]", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.warn("Unable to process match state input [{}]",ex.getMessage(), ex);
            session.close();
            sessions.remove(session);
        }
    }
}
