package com.neo.r2.ts.web.rss;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.api.rss.AbstractRssFeed;
import com.neo.r2.ts.impl.player.PlayerLookUpObject;
import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.r2.ts.impl.rss.RssHeader;
import com.neo.r2.ts.impl.rss.RssItem;
import com.neo.r2.ts.impl.rss.RssResponse;
import com.neo.r2.ts.web.rest.AuthorizationEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.text.MessageFormat;
import java.time.Instant;

@ApplicationScoped
@Path(AuthorizationEndpoint.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PlayerKillsRssFeed extends AbstractRssFeed {

    public static final String RESOURCE_LOCATION = "/api/v1/rss/player/kills";

    protected long id = 0;

    @Inject
    protected PlayerLookUpRepository playerLookUpRepository;

    @Override
    public String getId() {
        return "player-kills";
    }

    @Override
    protected RssHeader createHeader() {
        return new RssHeader(RESOURCE_LOCATION, getId(), "The last 20 Player Kills");
    }

    @Override
    protected int getMaxRssItems() {
        return 20;
    }

    @GET
    public RssResponse getRssResponse() {
        return new RssResponse(rssHeader, rssItems);
    }

    public void addPlayerKill(String attackerId, String victimId, String dmgType) {
        String attackerName = playerLookUpRepository.fetchByUId(attackerId).map(PlayerLookUpObject::playerName).orElse(CustomConstants.UNKNOWN);
        String victimName = playerLookUpRepository.fetchByUId(victimId).map(PlayerLookUpObject::playerName).orElse(CustomConstants.UNKNOWN);
        addRssItem(new RssItem(
                String.valueOf(id++), MessageFormat.format("{0} Killed {1}",attackerName , victimName),
                dmgType,
                Instant.now(),
                null));
    }
}
