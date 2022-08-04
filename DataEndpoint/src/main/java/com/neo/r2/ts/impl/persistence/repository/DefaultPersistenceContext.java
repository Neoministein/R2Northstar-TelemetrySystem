package com.neo.r2.ts.impl.persistence.repository;

import com.neo.util.framework.persistence.api.PersistenceContextService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class DefaultPersistenceContext implements PersistenceContextService {

    @PersistenceContext(unitName = "mainPersistence")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
