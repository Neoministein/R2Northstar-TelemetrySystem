package com.neo.r2.ts.web.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.impl.player.PlayerLookUpObject;
import com.neo.r2.ts.impl.player.PlayerSearchLookUpDto;
import com.neo.r2.ts.impl.repository.searchable.PlayerLookUpRepository;
import com.neo.r2.ts.web.rest.dto.outbound.HitsDto;
import com.neo.r2.ts.web.rest.dto.outbound.ValueDto;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.common.impl.json.JsonUtil;
import com.neo.util.framework.rest.api.cache.ClientCacheControl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path(PlayerResource.RESOURCE_LOCATION)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PlayerResource {

    public static final String RESOURCE_LOCATION = "api/v1/player";

    @Inject
    protected PlayerLookUpRepository playerLookUpRepository;

    @GET
    @Path("/uid/{uid}")
    @ClientCacheControl(maxAge = 1, timeUnit = TimeUnit.HOURS)
    public PlayerLookUpObject getPlayerNameByUid(@PathParam("uid") String uid) {
        return playerLookUpRepository.fetchByUId(uid)
                .orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_PLAYER_FOUND, uid));
    }

    @GET
    @Path("/name/{name}")
    @ClientCacheControl(maxAge = 1, timeUnit = TimeUnit.HOURS)
    public PlayerLookUpObject getUidByPlayerName(@PathParam("name") String playerName) {
        return playerLookUpRepository.fetchByPlayerName(playerName)
                .orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_PLAYER_FOUND, playerName));
    }


    @GET
    @Path("search/name/{name}")
    @ClientCacheControl(maxAge = 1, timeUnit = TimeUnit.HOURS)
    public HitsDto<PlayerLookUpObject> searchByPlayerName(@PathParam("name") String playerName) {
        return new HitsDto<>(playerLookUpRepository.searchByPlayerName(playerName));
    }

    @POST
    @Path("/uid/search")
    @ClientCacheControl(maxAge = 1, timeUnit = TimeUnit.HOURS)
    public ObjectNode searchForPlayerNameByUid(PlayerSearchLookUpDto playerLookUpDto) {
        ObjectNode result = JsonUtil.emptyObjectNode();
        for (String player: playerLookUpDto.playerUIds()) {
            result.put(player, playerLookUpRepository.fetchByUId(player).map(PlayerLookUpObject::playerName).orElse("UNKNOWN_PLAYER"));
        }

        return result;
    }

    @GET
    @Path("/all")
    public HitsDto<PlayerLookUpObject> allPlayers() {
        return new HitsDto<>(playerLookUpRepository.fetchAllPlayers());
    }

    @GET
    @Path("/unqiue")
    public ValueDto<Long> uniquePlayers() {
        return new ValueDto<>(playerLookUpRepository.countUniquePlayers());
    }
}
