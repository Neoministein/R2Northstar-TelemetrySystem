package com.neo.tf2.ms.impl.security;

import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.entity.EntityQuery;
import com.neo.javax.api.persitence.entity.EntityRepository;
import com.neo.javax.api.persitence.entity.EntityResult;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    protected static final EntityQuery<UserToken> ACTIVE_TOKENS = new EntityQuery<>(
            UserToken.class,
            List.of(new ExplicitSearchCriteria(UserToken.C_DISABLED, false)));

    protected static final long TEN_SEC = 1000L * 60L;


    protected Map<String, UserToken> tokenCache = new HashMap<>();
    protected long lastCheck = 0;

    @Inject
    EntityRepository entityRepository;

    @PostConstruct
    public void init() {
        LOGGER.info("Loading user tokens");
        Map<String, UserToken> newTokenCache = new HashMap<>();
        EntityResult<UserToken> result = entityRepository.find(ACTIVE_TOKENS);
        for (UserToken userToken: result.getHits()) {
            newTokenCache.put(userToken.getKey(), userToken);
        }
        LOGGER.debug("There are {} new user tokens and the last update was {} milliseconds ago", newTokenCache.size() - tokenCache.size(),  System.currentTimeMillis() - lastCheck);
        tokenCache = newTokenCache;
        lastCheck = System.currentTimeMillis();
    }

    public Optional<UserToken> retrieveToken(String token) {
        if (tokenCache.containsKey(token)) {
            return Optional.of(tokenCache.get(token));
        }
        return checkForNewKey(token);
    }

    protected synchronized Optional<UserToken> checkForNewKey(String token) {
        //There is a second contains since this method is synchronized and it could be added while the thread it waiting for the lock
        if (tokenCache.containsKey(token)) {
            return Optional.of(tokenCache.get(token));
        }
        if (lastCheck + TEN_SEC < System.currentTimeMillis()) {
            init();
            return retrieveToken(token);
        }
        return Optional.empty();
    }
}
