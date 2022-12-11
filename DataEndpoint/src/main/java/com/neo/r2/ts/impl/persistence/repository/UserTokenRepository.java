package com.neo.r2.ts.impl.persistence.repository;

import com.neo.r2.ts.impl.persistence.entity.UserToken;
import com.neo.util.framework.database.impl.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserTokenRepository extends BaseRepositoryImpl<UserToken> {

    public UserTokenRepository() {
        super(UserToken.class);
    }

    public Optional<UserToken> fetchByKey(String key) {
        String query = """
                SELECT t
                FROM UserToken t
                LEFT JOIN FETCH t.roles
                WHERE t.key =:""" + UserToken.C_KEY;
        try {
            return Optional.of(pcs.getEm().createQuery(query, UserToken.class)
                    .setParameter(UserToken.C_KEY, key).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<UserToken> fetchByOwner(UUID owner) {
        String query = """
                SELECT t
                FROM UserToken t
                LEFT JOIN FETCH t.roles
                WHERE t.owner =:""" + UserToken.C_OWNER;
        try {
            return Optional.of(pcs.getEm().createQuery(query, UserToken.class)
                    .setParameter(UserToken.C_OWNER, owner).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<UserToken> fetchByState(boolean isDisabled) {
        String query = """
                SELECT t
                FROM UserToken t
                LEFT JOIN FETCH t.roles
                WHERE t.disabled =:""" + UserToken.C_KEY;
        return pcs.getEm().createQuery(query, UserToken.class)
                .setParameter(UserToken.C_KEY, isDisabled).getResultList();
    }
}
