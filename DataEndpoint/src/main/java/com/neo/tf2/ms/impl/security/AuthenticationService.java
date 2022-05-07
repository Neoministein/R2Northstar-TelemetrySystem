package com.neo.tf2.ms.impl.security;

import com.neo.javax.api.persitence.criteria.ExplicitSearchCriteria;
import com.neo.javax.api.persitence.entity.EntityQuery;
import com.neo.javax.api.persitence.entity.EntityRepository;
import com.neo.tf2.ms.impl.persistence.entity.UserToken;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    protected static final EntityQuery<UserToken> ACTIVE_TOKENS = new EntityQuery<>(UserToken.class, List.of(new ExplicitSearchCriteria(UserToken.C_DISABLED, false)));
    protected static final long TEN_SEC = 1000L * 60L;


    protected Map<String, UserToken> tokenCache = new HashMap<>();
    protected long lastCheck = 0;

    @Inject
    EntityRepository entityRepository;

    @PostConstruct
    public void init() {
        Map<String, UserToken> newTokenCache = new HashMap<>();
        for (UserToken userToken: entityRepository.find(ACTIVE_TOKENS).getHits()) {
            tokenCache.put(userToken.getKey(), userToken);
        }
        tokenCache = newTokenCache;
        lastCheck = System.currentTimeMillis();
    }

    public Optional<UserToken> retrieveToken(String token) {
        if (tokenCache.containsKey(token)) {
            return Optional.of(tokenCache.get(token));
        }
        return checkForNewKey();
    }

    protected synchronized Optional<UserToken> checkForNewKey() {
        if (lastCheck + TEN_SEC < System.currentTimeMillis()) {
            init();
        }
        return Optional.empty();
    }
}
