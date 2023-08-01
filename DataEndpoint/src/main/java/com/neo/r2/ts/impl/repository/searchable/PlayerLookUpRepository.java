package com.neo.r2.ts.impl.repository.searchable;

import com.neo.r2.ts.impl.player.PlayerLookUpObject;
import com.neo.r2.ts.persistence.searchable.PlayerUidSearchable;
import com.neo.util.framework.api.cache.spi.CacheInvalidate;
import com.neo.util.framework.api.cache.spi.CacheKeyParameterPositions;
import com.neo.util.framework.api.cache.spi.CacheResult;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.search.SearchProvider;
import com.neo.util.framework.api.persistence.search.SearchQuery;
import com.neo.util.framework.api.persistence.search.SearchResult;
import com.neo.util.framework.elastic.api.IndexNamingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PlayerLookUpRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLookUpRepository.class);

    private static final String PLAYER_LOOK_UP_CACHE = "playerLookUp";

    protected String playerUid;

    @Inject
    protected SearchProvider searchProvider;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        playerUid = indexNamingService.getIndexNamePrefixFromClass(PlayerUidSearchable.class, true);
    }

    @CacheResult(cacheName = PLAYER_LOOK_UP_CACHE)
    public Optional<PlayerLookUpObject> fetchByUId(String uid) {
        LOGGER.info("Looking up player name uid [{}]", uid);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setMaxResults(1);
        searchQuery.setFields(List.of(PlayerUidSearchable.PLAYER_NAME, PlayerUidSearchable.U_ID));
        searchQuery.setFilters(List.of(new ExplicitSearchCriteria("_id",uid)));
        SearchResult<PlayerLookUpObject> result = searchProvider.fetch(playerUid, searchQuery, PlayerLookUpObject.class);

        return result.getHits().stream().findFirst();
    }

    public Optional<PlayerLookUpObject> fetchByPlayerName(String playerName) {
        LOGGER.info("Looking up uid by player name [{}]", playerName);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setMaxResults(1);
        searchQuery.setFields(List.of(PlayerUidSearchable.PLAYER_NAME, PlayerUidSearchable.U_ID));
        searchQuery.setFilters(List.of(new ExplicitSearchCriteria(PlayerUidSearchable.PLAYER_NAME, playerName)));
        SearchResult<PlayerLookUpObject> result = searchProvider.fetch(playerUid, searchQuery, PlayerLookUpObject.class);

        return result.getHits().stream().findFirst();
    }

    @CacheResult(cacheName = PLAYER_LOOK_UP_CACHE)
    public long countUniquePlayers() {
        return searchProvider.count(PlayerUidSearchable.class);
    }

    @CacheResult(cacheName = PLAYER_LOOK_UP_CACHE)
    public List<PlayerLookUpObject> fetchAllPlayers() {
        List<PlayerLookUpObject> resultList = new ArrayList<>();
        long numberQueries = countUniquePlayers() / 10_000 + 1;

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setMaxResults(10_000);
        searchQuery.setFields(List.of(PlayerUidSearchable.PLAYER_NAME, PlayerUidSearchable.U_ID));

        for (int i = 0; i < numberQueries;i++) {
            searchQuery.setOffset(i * 10_000);
            SearchResult<PlayerLookUpObject> result = searchProvider.fetch(playerUid, searchQuery, PlayerLookUpObject.class);
            resultList.addAll(result.getHits());
        }

        return resultList;
    }

    @CacheKeyParameterPositions(value = 0)
    @CacheInvalidate(cacheName = PLAYER_LOOK_UP_CACHE)
    public void updatePlayerLookUp(String uid, String playerName) {
        LOGGER.debug("Updating player PlayerLookUp uid [{}], playerName [{}]", uid, playerName);
        searchProvider.update(new PlayerUidSearchable(uid, playerName), true);
    }
}
