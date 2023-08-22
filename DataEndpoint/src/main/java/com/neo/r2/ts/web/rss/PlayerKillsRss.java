package com.neo.r2.ts.web.rss;

import com.neo.r2.ts.impl.rss.RssHeader;
import com.neo.r2.ts.impl.rss.RssItem;
import com.neo.r2.ts.impl.rss.RssResponse;
import com.neo.r2.ts.web.rest.AuthorizationEndpoint;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.LinkedList;

@ApplicationScoped
@Path(AuthorizationEndpoint.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PlayerKillsRss {

    public static final String RESOURCE_LOCATION = "/api/v1/rss/player/kills";

    protected long id = 0;

    protected RssHeader rssHeader;
    protected LinkedList<RssItem> rssItems = new LinkedList<>();

    @PostConstruct
    protected void init() {
        rssHeader = new RssHeader(RESOURCE_LOCATION, "Player Kills", "The last 20 Player Kills");
    }

    @GET
    public RssResponse getRssResponse() {
        return new RssResponse(rssHeader, rssItems);
    }

    public void addPlayerKill(String attackerName, String victimName, String dmgType) {

        addRssItem(new RssItem(String.valueOf(id++), MessageFormat.format("{0} Killed {1}", attackerName, victimName), dmgType, Instant.now() ,null));
    }

    protected void addRssItem(RssItem rssItem) {
        rssItems.addFirst(rssItem);
        rssItems.removeLast();
    }
}
