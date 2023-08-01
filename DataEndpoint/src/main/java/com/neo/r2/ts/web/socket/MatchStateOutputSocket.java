package com.neo.r2.ts.web.socket;

import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.impl.monitoring.AbstractMonitorableWebsocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = MatchStateOutputSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class MatchStateOutputSocket extends AbstractMonitorableWebsocket {

    public static final String WS_LOCATION = "/ws/state/output/{id}";

    protected Map<String, List<Session>> sessionMap = new ConcurrentHashMap<>();

    @Override
    protected void onOpen(Session session) {
        String id = getPathParameter(session, "id");
        List<Session> sessions = sessionMap.get(id);
        if (sessions == null) {
            sessions = new ArrayList<>();
            sessions.add(session);
            sessionMap.put(id, sessions);
        } else {
            sessions.add(session);
        }
    }

    @Override
    protected void onClose(Session session) {
        String id = getPathParameter(session, "id");
        List<Session> sessions = sessionMap.computeIfPresent(id, (key, val) -> {
            val.remove(session);
            return val;
        });
        if (sessions != null && sessions.isEmpty()) {
            sessionMap.remove(id);
        }
    }

    public void broadcast(String id ,String message) {
        sessionMap.computeIfPresent(id, (key, val) -> {
            val.forEach(session -> super.broadcast(session, message));
            return val;
        });
    }
}
