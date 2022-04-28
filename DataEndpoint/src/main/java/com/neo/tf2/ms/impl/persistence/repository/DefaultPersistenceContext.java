package com.neo.tf2.ms.impl.persistence.repository;


import com.neo.javax.api.persistence.repository.PersistenceContextService;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class DefaultPersistenceContext implements PersistenceContextService {

    @PersistenceContext(unitName = "mainPersistence")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
