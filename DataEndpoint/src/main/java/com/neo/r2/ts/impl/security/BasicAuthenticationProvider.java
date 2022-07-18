package com.neo.r2.ts.impl.security;

import com.neo.r2.ts.impl.persistence.entity.UserToken;
import com.neo.util.framework.api.PriorityConstants;
import com.neo.util.framework.api.persistence.criteria.ExplicitSearchCriteria;
import com.neo.util.framework.api.persistence.entity.EntityQuery;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import com.neo.util.framework.api.persistence.entity.EntityResult;
import com.neo.util.framework.api.security.AuthenticationProvider;
import com.neo.util.framework.api.security.AuthenticationScheme;
import com.neo.util.framework.api.security.RolePrincipal;
import com.neo.util.framework.api.security.credential.BearerCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Alternative
@Priority(PriorityConstants.APPLICATION)
@ApplicationScoped
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    protected static final EntityQuery<UserToken> ACTIVE_TOKENS = new EntityQuery<>(
            UserToken.class,
            List.of(new ExplicitSearchCriteria(UserToken.C_DISABLED, false)));

    protected static final long TEN_SEC = 1000L * 60L;


    protected Map<String, UserToken> tokenCache = new HashMap<>();
    protected long lastCheck = 0;

    @Inject
    protected EntityRepository entityRepository;

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


    @Override
    public Optional<RolePrincipal> authenticate(Credential credential) {
        if (credential instanceof BearerCredentials) {
            return authenticate(((BearerCredentials) credential).getToken());
        }
        return Optional.empty();
    }

    @Override
    public List<String> getSupportedAuthenticationSchemes() {
        return List.of(AuthenticationScheme.BEARER);
    }

    protected Optional<RolePrincipal> authenticate(String token) {
        if (tokenCache.containsKey(token)) {
            return Optional.of(tokenCache.get(token));
        }
        return checkForNewKey((token));
    }

    protected synchronized Optional<RolePrincipal> checkForNewKey(String token) {
        //There is a second contains since this method is synchronized, and it could be added while the thread it's waiting for the lock
        if (tokenCache.containsKey(token)) {
            return Optional.of(tokenCache.get(token));
        }
        if (lastCheck + TEN_SEC < System.currentTimeMillis()) {
            init();
            return authenticate(token);
        }
        return Optional.empty();
    }
}
