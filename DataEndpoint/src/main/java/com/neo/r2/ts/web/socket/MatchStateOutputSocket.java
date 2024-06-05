package com.neo.r2.ts.web.socket;

import com.neo.r2.ts.impl.match.MatchStatusEvent;
import com.neo.util.framework.websocket.api.NeoUtilWebsocket;
import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.api.WebsocketStateContext;
import com.neo.util.framework.websocket.impl.WebsocketUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NeoUtilWebsocket
@ApplicationScoped
@ServerEndpoint(value = MatchStateOutputSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class MatchStateOutputSocket {

    public static final String WS_LOCATION = "/ws/v1/state/output/{id}";

    protected static final String BROADCAST_END = "MATCH_END";

    protected Map<String, List<WebsocketStateContext>> stateContextMap = new ConcurrentHashMap<>();

    public void matchStatusEvent(@Observes MatchStatusEvent matchStatusEvent) throws IOException {
        if (MatchStatusEvent.Type.CREATED.equals(matchStatusEvent.type())) {
            stateContextMap.put(matchStatusEvent.matchId(), new ArrayList<>());
        } else if (MatchStatusEvent.Type.ENDED.equals(matchStatusEvent.type())) {
            for (WebsocketStateContext context: stateContextMap.remove(matchStatusEvent.matchId())) {
                context.broadcast(BROADCAST_END);
                context.getSession().close();
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("id") String id) throws IOException {
        List<WebsocketStateContext> contexts = stateContextMap.get(id);
        if (contexts == null) {
            session.close();
        } else {
            contexts.add(WebsocketUtil.getWebsocketContext(session));
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        List<WebsocketStateContext> contexts = stateContextMap.get(id);
        if (contexts != null) {
            contexts.remove(WebsocketUtil.getWebsocketContext(session));
        }
    }

    public void broadcast(String id ,String message) {
        for (WebsocketStateContext context: stateContextMap.get(id)) {
            context.broadcastAsync(message);
        }
    }
}
