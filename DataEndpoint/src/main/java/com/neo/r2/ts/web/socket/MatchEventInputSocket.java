package com.neo.r2.ts.web.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.MatchStatusEvent;
import com.neo.r2.ts.impl.match.event.MatchEvent;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.neo.util.framework.websocket.api.NeoUtilWebsocket;
import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.api.WebsocketStateContext;
import com.neo.util.framework.websocket.impl.WebsocketUtil;
import com.networknt.schema.JsonSchema;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NeoUtilWebsocket(secured = true, monitored = true)
@ApplicationScoped
@ServerEndpoint(value = MatchEventInputSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class MatchEventInputSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventInputSocket.class);

    public static final String WS_LOCATION = "/ws/v1/state/input/{id}";

    protected final Map<String, WebsocketStateContext> stateContextMap;
    protected final JsonSchema eventSchema;
    protected final MatchEventService matchEventService;

    @Inject
    public MatchEventInputSocket(JsonSchemaLoader schemaLoader, MatchEventService matchEventService) {
        this.eventSchema = schemaLoader.getUnmodifiableMap().get("state/Event.json");
        this.matchEventService = matchEventService;
        this.stateContextMap = new ConcurrentHashMap<>();
    }

    public void matchStatusEvent(@Observes MatchStatusEvent matchStatusEvent) throws IOException {
        if (MatchStatusEvent.Type.CREATED.equals(matchStatusEvent.type())) {
            stateContextMap.put(matchStatusEvent.matchId(), null);
        } else if (MatchStatusEvent.Type.ENDED.equals(matchStatusEvent.type())) {
            WebsocketStateContext context = stateContextMap.remove(matchStatusEvent.matchId());
            if (context != null) {
                context.getSession().close();
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("id") String id) throws IOException {
        if (stateContextMap.containsKey(id)) {
            stateContextMap.put(id, WebsocketUtil.getWebsocketContext(session));
        } else {
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("id") String id, String message) throws IOException {
        try {
            JsonNode event = JsonUtil.fromJson(message);
            JsonSchemaUtil.isValidOrThrow(event, eventSchema);
            matchEventService.delegateToEventProcessor(new MatchEvent(id ,event));
        } catch (ValidationException ex) {
            LOGGER.warn("A validation exception occurred [{}], body [{}]", ex.getMessage(), message);
        } catch (Exception ex) {
            LOGGER.warn("Unable to process match state input [{}]",ex.getMessage(), ex);
            session.close();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        stateContextMap.remove(id);
    }
}
