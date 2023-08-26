package com.neo.r2.ts.web.socket;

import com.neo.r2.ts.impl.rss.RssItem;
import com.neo.r2.ts.api.rss.RssFeed;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.websocket.api.WebserverHttpHeaderForwarding;
import com.neo.util.framework.websocket.impl.monitoring.AbstractMonitorableWebsocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = RssFeedSocket.WS_LOCATION, configurator = WebserverHttpHeaderForwarding.class)
public class RssFeedSocket extends AbstractMonitorableWebsocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedSocket.class);

    public static final String WS_LOCATION = "/ws/v1/rss/{id}";

    protected Map<String, List<Session>> sessionMap = new ConcurrentHashMap<>();

    public void init(Instance<RssFeed> rssFeeds) {
        for (RssFeed rssFeed: rssFeeds) {
            sessionMap.put(rssFeed.getId(), new ArrayList<>());
        }
    }

    @Override
    protected void onOpen(Session session) throws IOException {
        String id = getPathParameter(session, "id");
        List<Session> sessions = sessionMap.get(id);
        if (sessions == null) {
            session.close();
        } else {
            sessions.add(session);
        }
    }

    @Override
    protected void onClose(Session session) {
        String id = getPathParameter(session, "id");
        sessionMap.get(id).remove(session);
    }

    public void broadcast(String rssId, RssItem rssItem) {
        String message = JsonUtil.toJson(rssItem);
        sessionMap.computeIfPresent(rssId, (key, val) -> {
            val.forEach(session -> super.broadcast(session, message));
            return val;
        });
    }
}
