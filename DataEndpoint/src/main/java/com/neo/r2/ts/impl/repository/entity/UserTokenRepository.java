package com.neo.r2.ts.impl.repository.entity;

import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.database.impl.AbstractDatabaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserTokenRepository extends AbstractDatabaseRepository<ApplicationUser> {

    public UserTokenRepository() {
        super(ApplicationUser.class);
    }

    public Optional<ApplicationUser> fetchByKey(String key) {
        String query = """
                SELECT t
                FROM ApplicationUser t
                LEFT JOIN FETCH t.roles
                WHERE t.apiKey =:apiKey""";
        try {
            return Optional.of(pcs.getEm().createQuery(query, ApplicationUser.class)
                    .setParameter(ApplicationUser.C_API_KEY, key).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ApplicationUser> fetchByUid(String uId) {
        Optional<UUID> optUId = FrameworkMapping.optionalUUID(uId);
        if (optUId.isEmpty()) {
            return Optional.empty();
        }

        String query = """
                SELECT t
                FROM ApplicationUser t
                LEFT JOIN FETCH t.roles
                WHERE t.uid =:uid""";
        try {
            return Optional.of(pcs.getEm().createQuery(query, ApplicationUser.class)
                    .setParameter(ApplicationUser.C_UID, optUId.get()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<ApplicationUser> fetchByState(boolean isDisabled) {
        String query = """
                SELECT t
                FROM ApplicationUser t
                LEFT JOIN FETCH t.roles
                WHERE t.disabled =:disabled""";
        return pcs.getEm().createQuery(query, ApplicationUser.class)
                .setParameter(ApplicationUser.C_DISABLED, isDisabled).getResultList();
    }
}
