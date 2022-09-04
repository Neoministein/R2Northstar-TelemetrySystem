package com.neo.r2.ts.impl.persistence.repository;

import com.neo.util.framework.database.api.PersistenceContextService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class DefaultPersistenceContext implements PersistenceContextService {

    @PersistenceContext(unitName = "mainPersistence")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
