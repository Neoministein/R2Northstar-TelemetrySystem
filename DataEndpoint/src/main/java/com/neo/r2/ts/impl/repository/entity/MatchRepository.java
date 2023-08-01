package com.neo.r2.ts.impl.repository.entity;

import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.database.impl.AbstractDatabaseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatchRepository extends AbstractDatabaseRepository<Match> {

    public MatchRepository() {
        super(Match.class);
    }

    public Optional<Match> fetch(String id) {
        return FrameworkMapping.optionalUUID(id).flatMap(super::fetch);
    }

    public List<Match> fetchArePlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = true
                ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> fetchStoppedPlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = false
                ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> fetchArePlaying(Instant cutOfDate) {
        String query = """
               SELECT m
               FROM Match m
               WHERE m.isRunning = true AND m.createdOn < :cutOfDate
               ORDER BY m.createdOn desc""";
        return pcs.getEm().createQuery(query, Match.class).setParameter("cutOfDate", cutOfDate).getResultList();
    }
}
