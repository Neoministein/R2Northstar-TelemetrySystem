package com.neo.r2.ts.impl.security;

import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.r2.ts.impl.repository.UserTokenRepository;
import com.neo.util.framework.api.PriorityConstants;
import com.neo.util.framework.api.security.AuthenticationProvider;
import com.neo.util.framework.api.security.AuthenticationScheme;
import com.neo.util.framework.api.security.RolePrincipal;
import com.neo.util.framework.api.security.credential.BearerCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Alternative
@Priority(PriorityConstants.APPLICATION)
@ApplicationScoped
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    protected static final long TEN_SEC = 1000L * 60L;


    protected Map<String, ApplicationUser> tokenCache = new HashMap<>();
    protected long lastCheck = 0;

    @Inject
    protected UserTokenRepository userTokenRepository;

    @PostConstruct
    public void init() {
        LOGGER.info("Loading user tokens");
        Map<String, ApplicationUser> newTokenCache = new HashMap<>();
        for (ApplicationUser userToken: userTokenRepository.fetchByState(false)) {
            newTokenCache.put(userToken.getApiKey(), userToken);
        }
        LOGGER.debug("There are {} new user tokens and the last update was {} milliseconds ago", newTokenCache.size() - tokenCache.size(),  System.currentTimeMillis() - lastCheck);
        tokenCache = newTokenCache;
        lastCheck = System.currentTimeMillis();
    }


    @Override
    public Optional<RolePrincipal> authenticate(Credential credential) {
        if (credential instanceof BearerCredentials bearerCredentials) {
            return authenticate((bearerCredentials).getToken());
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
