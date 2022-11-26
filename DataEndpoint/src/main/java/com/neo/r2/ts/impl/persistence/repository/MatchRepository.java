package com.neo.r2.ts.impl.persistence.repository;

import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.framework.database.impl.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MatchRepository extends BaseRepositoryImpl<Match> {

    public MatchRepository() {
        super(Match.class);
    }

    public Optional<Match> getMatchById(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        try {
            return fetch(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public List<Match> getArePlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = true
                ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> getStoppedPlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = false 
                ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> getArePlaying(Date cutOfDate) {
        String query = """
               SELECT m FROM
               Match m WHERE m.isRunning = true AND m.createdOn < :cutOfDate
               ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).setParameter("cutOfDate", cutOfDate).getResultList();
    }
}
