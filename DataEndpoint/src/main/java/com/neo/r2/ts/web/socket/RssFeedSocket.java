package com.neo.r2.ts.web.socket;

import com.neo.r2.ts.api.rss.RssFeed;
import com.neo.r2.ts.api.rss.RssItem;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.websocket.api.NeoUtilWebsocket;
import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.api.WebsocketStateContext;
import com.neo.util.framework.websocket.impl.WebsocketUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
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
@ServerEndpoint(value = RssFeedSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class RssFeedSocket {

    public static final String WS_LOCATION = "/ws/v1/rss/{id}";

    protected final Map<String, List<WebsocketStateContext>> sessionMap = new ConcurrentHashMap<>();

    @Inject
    public RssFeedSocket(Instance<RssFeed> rssFeeds) {
        for (RssFeed rssFeed: rssFeeds) {
            sessionMap.put(rssFeed.getId(), new ArrayList<>());
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("id") String id) throws IOException {
        List<WebsocketStateContext> sessions = sessionMap.get(id);
        if (sessions == null) {
            session.close();
        } else {
            sessions.add(WebsocketUtil.getWebsocketContext(session));
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        sessionMap.get(id).remove(WebsocketUtil.getWebsocketContext(session));
    }

    public void broadcast(String rssId, RssItem rssItem) {
        String message = JsonUtil.toJson(rssItem);
        sessionMap.computeIfPresent(rssId, (key, val) -> {
            val.forEach(context -> context.broadcastAsync(message));
            return val;
        });
    }
}
