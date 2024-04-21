package com.neo.r2.ts.impl.rss;

import com.neo.r2.ts.api.rss.RssFeed;
import com.neo.r2.ts.api.rss.RssHeader;
import com.neo.r2.ts.api.rss.RssItem;
import com.neo.r2.ts.api.rss.RssResponse;
import com.neo.r2.ts.web.socket.RssFeedSocket;
import jakarta.ws.rs.GET;

import java.util.LinkedList;

public abstract class AbstractRssFeed implements RssFeed {

    protected final int maxItems;
    protected final RssHeader rssHeader;
    protected final RssFeedSocket rssFeedSocket;

    protected LinkedList<RssItem> rssItems = new LinkedList<>();

    protected AbstractRssFeed(RssHeader rssHeader, int maxItems, RssFeedSocket rssFeedSocket) {
        this.rssHeader = rssHeader;
        this.maxItems = maxItems;
        this.rssFeedSocket = rssFeedSocket;
    }

    @GET
    public RssResponse getRssResponse() {
        return new RssResponse(rssHeader, rssItems);
    }

    protected void addRssItem(RssItem rssItem) {
        rssFeedSocket.broadcast(getId(), rssItem);
        rssItems.addFirst(rssItem);
        if (rssItems.size() > maxItems) {
            rssItems.removeLast();
        }
    }
}
