package com.neo.r2.ts.web.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.player.PlayerLookUpObject;
import com.neo.r2.ts.impl.player.PlayerLookUpService;
import com.neo.r2.ts.impl.player.PlayerSearchLookUpDto;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path(PlayerResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PlayerResource {

    public static final String RESOURCE_LOCATION = "api/v1/player";

    protected static final int SECONDS_IN_DAY = 86400;

    @Inject
    protected PlayerLookUpService playerLookUpService;

    @GET
    @Path("/uid/{uid}")
    @ClientCacheControl(maxAge = SECONDS_IN_DAY)
    public PlayerLookUpObject getPlayerNameByUid(@PathParam("uid") String uid) {
        return playerLookUpService.fetchByUId(uid)
                .orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_PLAYER_FOUND, uid));
    }

    @GET
    @Path("/name/{name}")
    @ClientCacheControl(maxAge = SECONDS_IN_DAY)
    public PlayerLookUpObject getUidByPlayerName(@PathParam("name") String playerName) {
        return playerLookUpService.fetchByPlayerName(playerName)
                .orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_PLAYER_FOUND, playerName));
    }

    @POST
    @Path("/uid/search")
    @ClientCacheControl(maxAge = SECONDS_IN_DAY)
    public ObjectNode searchForPlayerNameByUid(PlayerSearchLookUpDto playerLookUpDto) {
        ObjectNode result = JsonUtil.emptyObjectNode();
        for (String player: playerLookUpDto.playerUIds()) {
            result.put(player, playerLookUpService.fetchByUId(player).map(PlayerLookUpObject::playerName).orElse("UNKNOWN_PLAYER"));
        }

        return result;
    }
}
