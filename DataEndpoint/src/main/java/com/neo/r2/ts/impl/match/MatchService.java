package com.neo.r2.ts.impl.match;

import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.r2.ts.impl.persistence.entity.Match;
import com.neo.util.common.impl.StringUtils;
import com.neo.util.common.impl.exception.InternalLogicException;
import com.neo.util.framework.api.persistence.entity.EntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;


@Transactional
@ApplicationScoped
public class MatchService {

    @Inject
    protected EntityRepository entityRepository;

    public Optional<Match> getMatch(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        try {
            Optional<Match> optionalMatch = entityRepository.find(UUID.fromString(id), Match.class);
            optionalMatch.ifPresent(match -> match.getHeatmaps().size());
            return optionalMatch;
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
    public void addHeatmap(Match match, Heatmap heatmap) {
        match.getHeatmaps().add(heatmap);
        heatmap.setMatch(match);
        try {
            entityRepository.edit(match);
        } catch (RollbackException ex) {
            throw new InternalLogicException("Cannot save heatmap rollback exception", ex);
        }
    }
}
