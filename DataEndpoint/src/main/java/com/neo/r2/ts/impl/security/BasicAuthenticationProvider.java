package com.neo.r2.ts.impl.security;

import com.neo.r2.ts.impl.repository.UserTokenRepository;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.util.framework.api.PriorityConstants;
import com.neo.util.framework.api.security.AuthenticationProvider;
import com.neo.util.framework.api.security.AuthenticationScheme;
import com.neo.util.framework.api.security.RolePrincipal;
import com.neo.util.framework.api.security.credential.BearerCredentials;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Alternative
@Priority(PriorityConstants.APPLICATION)
@ApplicationScoped
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    protected Map<String, ApplicationUser> tokenCache = new HashMap<>();
    protected Instant lastTokenUpdate = Instant.now();

    @Inject
    protected UserTokenRepository userTokenRepository;

    @PostConstruct
    public void reloadUserTokens() {
        LOGGER.info("Loading Users...");
        Map<String, ApplicationUser> newTokenCache = new HashMap<>();
        for (ApplicationUser userToken: userTokenRepository.fetchByState(false)) {
            newTokenCache.put(userToken.getApiKey(), userToken);
        }
        LOGGER.debug("There are [{}] new user, last update was [{}] seconds ago", newTokenCache.size() - tokenCache.size(), Instant.now().getEpochSecond() - lastTokenUpdate.getEpochSecond());
        tokenCache = newTokenCache;
        lastTokenUpdate = Instant.now();
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
        return Optional.ofNullable((RolePrincipal) tokenCache.get(token)).or(() -> checkForNewKey(token));
    }

    protected synchronized Optional<RolePrincipal> checkForNewKey(String token) {
        //There is a second check since this method is synchronized, and it could be added while the thread it's waiting for the lock
        Optional<RolePrincipal> optUser = Optional.ofNullable(tokenCache.get(token));

        if (optUser.isPresent()) {
            return optUser;
        }

        if (lastTokenUpdate.plus(Duration.ofSeconds(60)).isBefore(Instant.now())) {
            reloadUserTokens();
            return authenticate(token);
        }
        return Optional.empty();
    }
}
