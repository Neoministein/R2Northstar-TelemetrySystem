package com.neo.r2.ts.impl.repository;

import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.util.framework.database.impl.AbstractDatabaseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class HeatmapRepository extends AbstractDatabaseRepository<Heatmap> {

    public HeatmapRepository() {
        super(Heatmap.class);
    }

    public Optional<Heatmap> fetchFullMapAggregation(String map) {
        String query = """
                SELECT m
                FROM Heatmap m
                WHERE m.map = :map
                AND m.match IS NULL
                ORDER BY m.id desc""";
        return pcs.getEm().createQuery(query, Heatmap.class)
                .setParameter(Heatmap.C_MAP, map)
                .setMaxResults(1).getResultList().stream().findFirst();
    }
}
