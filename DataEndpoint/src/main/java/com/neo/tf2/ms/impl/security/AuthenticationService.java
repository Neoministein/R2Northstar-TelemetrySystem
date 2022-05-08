package com.neo.tf2.ms.impl.security;

import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.entity.EntityQuery;
import com.neo.javax.api.persitence.entity.EntityRepository;
import com.neo.javax.api.persitence.entity.EntityResult;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

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
        Map<String, UserToken> newTokenCache = new HashMap<>();
        EntityResult<UserToken> result = entityRepository.find(ACTIVE_TOKENS);
        for (UserToken userToken: result.getHits()) {
            newTokenCache.put(userToken.getKey(), userToken);
        }
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
