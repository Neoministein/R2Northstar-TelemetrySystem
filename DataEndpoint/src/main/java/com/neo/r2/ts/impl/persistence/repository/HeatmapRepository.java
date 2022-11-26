package com.neo.r2.ts.impl.persistence.repository;

import com.neo.r2.ts.impl.persistence.entity.Heatmap;
import com.neo.util.framework.database.impl.repository.BaseRepositoryImpl;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HeatmapRepository extends BaseRepositoryImpl<Heatmap> {

    public HeatmapRepository() {
        super(Heatmap.class);
    }

    public List<Heatmap> getHeatmapOfMap(String map) {
        String query = """
                SELECT m
                FROM Heatmap m
                WHERE m.map = :map
                ORDER BY m.id desc""";
        return pcs.getEm().createQuery(query, Heatmap.class).setParameter(":map", map).getResultList();
    }
}
