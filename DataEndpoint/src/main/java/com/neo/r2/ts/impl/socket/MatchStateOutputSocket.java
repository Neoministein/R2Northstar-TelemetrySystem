package com.neo.r2.ts.impl.socket;

import com.neo.r2.ts.impl.match.MatchStateService;
import com.neo.util.common.impl.json.JsonUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.elasticsearch.common.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint(value = MatchStateOutputSocket.WS_LOCATION)
public class MatchStateOutputSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchStateOutputSocket.class);

    public static final String WS_LOCATION = "/ws/state/output/{id}";

    protected Map<String, List<Session>> sessionMap = new HashMap<>();

    @Inject
    protected MatchStateService matchStateService;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        List<Session> sessions = sessionMap.putIfAbsent(id, new ArrayList<>());
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        List<Session> sessions = sessionMap.computeIfPresent(id, (key, val) -> {
            val.remove(session);
            return val;
        });
        if (sessions.isEmpty()) {
            sessionMap.remove(id);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.warn("There has been an error with session [{}] [{}]", session.getId(), throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            matchStateService.updateGameState(JsonUtil.fromJson(message));
        } catch (Exception ex) {
            LOGGER.warn("Unable to process match state input [{}]", ex.getMessage());
        }
    }

    public void broadcast(String id ,String message) {
        sessionMap.computeIfPresent(id, (key, val) -> {
            val.forEach(session -> {
                try {
                    session.getBasicRemote().sendObject(message);
                } catch (IOException | EncodeException ex) {
                    LOGGER.warn("Unable to brodcast message to session [{}] [{}]", session.getId(), ex.getMessage());
                }
            });
            return val;
        });
    }
}
