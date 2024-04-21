package com.neo.r2.ts.web.rss;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.rss.RssHeader;
import com.neo.r2.ts.api.rss.RssItem;
import com.neo.r2.ts.impl.player.PlayerLookUpObject;
import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.r2.ts.impl.rss.AbstractRssFeed;
import com.neo.r2.ts.web.socket.RssFeedSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Instant;

@ApplicationScoped
@Path(PlayerKillsRssFeed.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PlayerKillsRssFeed extends AbstractRssFeed {

    public static final String RESOURCE_LOCATION = "/api/v1/rss/player-kills";

    public static final RssHeader RSS_HEADER = new RssHeader(RESOURCE_LOCATION, "player-kills", "The last 20 Player Kills");

    protected final PlayerLookUpRepository playerLookUpRepository;

    protected long id = 0;

    @Inject
    protected PlayerKillsRssFeed(PlayerLookUpRepository playerLookUpRepository, RssFeedSocket rssFeedSocket) {
        super(RSS_HEADER, 20, rssFeedSocket);
        this.playerLookUpRepository = playerLookUpRepository;
    }

    @Override
    public String getId() {
        return RSS_HEADER.title();
    }

    public void addPlayerKill(String attackerId, String victimId, String dmgType) {
        String attackerName = playerLookUpRepository.fetchByUId(attackerId).map(PlayerLookUpObject::playerName).orElse(CustomConstants.UNKNOWN);
        String victimName = playerLookUpRepository.fetchByUId(victimId).map(PlayerLookUpObject::playerName).orElse(CustomConstants.UNKNOWN);
        addRssItem(new RssItem(
                String.valueOf(id++),
                STR."\{attackerName} Killed \{victimName}",
                dmgType,
                Instant.now(),
                null));
    }
}
