package com.neo.r2.ts.impl.repository;

import com.neo.util.framework.database.api.PersistenceContextProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class DefaultPersistenceContext implements PersistenceContextProvider {

    @PersistenceContext(unitName = "mainPersistence")
    private EntityManager em;

    @Override
    public EntityManager getEm() {
        return em;
    }
}
