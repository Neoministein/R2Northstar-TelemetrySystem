package com.neo.r2.ts.impl.repository.entity;

import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.database.api.PersistenceContextProvider;
import com.neo.util.framework.database.impl.AbstractDatabaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.neo.util.framework.rest.impl.entity.AbstractEntityRestEndpoint.EX_ENTITY_NOT_FOUND;

@ApplicationScoped
public class ApplicationRepository extends AbstractDatabaseRepository<ApplicationUser> {

    @Inject
    public ApplicationRepository(PersistenceContextProvider pcp) {
        super(pcp, ApplicationUser.class);
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
            return Optional.of(pcp.getEm().createQuery(query, ApplicationUser.class)
                    .setParameter(ApplicationUser.C_UID, optUId.get()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public ApplicationUser requestByUid(String uId) {
        return fetchByUid(uId).orElseThrow(() -> new NoContentFoundException(EX_ENTITY_NOT_FOUND, uId));
    }

    public List<ApplicationUser> fetchByState(boolean isDisabled) {
        String query = """
                SELECT t
                FROM ApplicationUser t
                LEFT JOIN FETCH t.roles
                WHERE t.disabled =:disabled""";
        return pcp.getEm().createQuery(query, ApplicationUser.class)
                .setParameter(ApplicationUser.C_DISABLED, isDisabled).getResultList();
    }
}
