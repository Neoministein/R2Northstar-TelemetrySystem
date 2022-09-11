package com.neo.r2.ts.impl.socket;

import com.neo.r2.ts.api.socket.AbstractMonitorableWebsocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint(value = MatchStateOutputSocket.WS_LOCATION)
public class MatchStateOutputSocket extends AbstractMonitorableWebsocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateOutputSocket.class);

    public static final String WS_LOCATION = "/ws/state/output/{id}";

    protected Map<String, List<Session>> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        List<Session> sessions = sessionMap.get(id);
        if (sessions == null) {
            sessions = new ArrayList<>();
            sessions.add(session);
            sessionMap.put(id, sessions);
        } else {
            sessions.add(session);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        List<Session> sessions = sessionMap.computeIfPresent(id, (key, val) -> {
            val.remove(session);
            return val;
        });
        if (sessions != null && sessions.isEmpty()) {
            sessionMap.remove(id);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.warn("There has been an error with session [{}] [{}]", session.getId(), throwable.getMessage());
    }

    public void broadcast(String id ,String message) {
        sessionMap.computeIfPresent(id, (key, val) -> {
            val.forEach(session -> super.broadcast(session, message));
            return val;
        });
    }

    @Override
    protected void handleIncomingMessage(String message) {
        //Noting needs to be done on message
    }
}
