package com.neo.r2.ts.api.rss;

import com.neo.r2.ts.impl.rss.RssHeader;
import com.neo.r2.ts.impl.rss.RssItem;
import com.neo.r2.ts.impl.rss.RssResponse;
import com.neo.r2.ts.web.socket.RssFeedSocket;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;

import java.util.LinkedList;

public abstract class AbstractRssFeed implements RssFeed {

    @Inject
    protected RssFeedSocket rssFeedSocket;

    protected RssHeader rssHeader;
    protected LinkedList<RssItem> rssItems = new LinkedList<>();

    protected abstract RssHeader createHeader();

    protected abstract int getMaxRssItems();

    @PostConstruct
    protected void init() {
        rssHeader = createHeader();
    }

    @GET
    public RssResponse getRssResponse() {
        return new RssResponse(rssHeader, rssItems);
    }

    protected void addRssItem(RssItem rssItem) {
        rssFeedSocket.broadcast(getId(), rssItem);
        rssItems.addFirst(rssItem);
        if (rssItems.size() > getMaxRssItems()) {
            rssItems.removeLast();
        }
    }
}
