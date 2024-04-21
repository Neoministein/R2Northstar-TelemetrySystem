package com.neo.r2.ts.impl.repository.entity;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.common.impl.exception.NoContentFoundException;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.database.api.PersistenceContextProvider;
import com.neo.util.framework.database.impl.AbstractDatabaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatchRepository extends AbstractDatabaseRepository<Match> {

    @Inject
    public MatchRepository(PersistenceContextProvider pcp) {
        super(pcp,Match.class);
    }

    public Optional<Match> fetchById(String id) {
        return FrameworkMapping.optionalUUID(id).flatMap(super::fetch);
    }

    public Match requestById(String id) {
        return fetchById(id).orElseThrow(() -> new NoContentFoundException(CustomConstants.EX_MATCH_NON_EXISTENT, id));
    }

    public List<Match> fetchArePlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = true
                ORDER BY m.createdOn desc""";
        return pcp.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> fetchStoppedPlaying() {
        String query = """
                SELECT m
                FROM Match m
                WHERE m.isRunning = false
                ORDER BY m.createdOn desc""";
        return pcp.getEm().createQuery(query, Match.class).getResultList();
    }

    public List<Match> fetchArePlaying(Instant cutOfDate) {
        String query = """
               SELECT m
               FROM Match m
               WHERE m.isRunning = true AND m.createdOn < :cutOfDate
               ORDER BY m.createdOn desc""";
        return pcp.getEm().createQuery(query, Match.class).setParameter("cutOfDate", cutOfDate).getResultList();
    }
}
