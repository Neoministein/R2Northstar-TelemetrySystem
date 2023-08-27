package com.neo.r2.ts.web.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.match.event.MatchEventService;
import com.neo.util.common.impl.exception.ValidationException;
import com.neo.util.common.impl.json.JsonSchemaUtil;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.impl.json.JsonSchemaLoader;
import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.impl.monitoring.AbstractMonitorableWebsocket;
import com.networknt.schema.JsonSchema;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
@ServerEndpoint(value = MatchEventInputSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class MatchEventInputSocket extends AbstractMonitorableWebsocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEventInputSocket.class);

    public static final String WS_LOCATION = "/ws/v1/state/input/{id}";

    protected JsonSchema eventSchema;

    @Inject
    protected MatchEventService matchEventService;

    @Inject
    protected JsonSchemaLoader schemaLoader;

    @PostConstruct
    public void init() {
        eventSchema = schemaLoader.getUnmodifiableMap().get("state/Event.json");
    }

    @Override
    public void onMessage(Session session, String message) throws IOException {
        super.onMessage(session, message);
        try {
            JsonNode event = JsonUtil.fromJson(message);
            JsonSchemaUtil.isValidOrThrow(event, eventSchema);
            matchEventService.delegateToEventProcessor(getPathParameter(session, "id"), event);
        } catch (ValidationException ex) {
            LOGGER.warn("A validation exception occurred [{}], body [{}]", ex.getMessage(), message);
        } catch (Exception ex) {
            LOGGER.warn("Unable to process match state input [{}]",ex.getMessage(), ex);
            session.close();
        }
    }

    @Override
    protected boolean secured() {
        return true;
    }
}
