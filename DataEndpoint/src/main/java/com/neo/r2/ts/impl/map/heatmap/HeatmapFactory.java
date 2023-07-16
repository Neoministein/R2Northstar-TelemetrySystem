package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.persistence.HeatmapEnums;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.r2.ts.persistence.entity.Match;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HeatmapFactory {

    public Heatmap createForMatch(Match match, HeatmapEnums.Type type) {
        Heatmap heatmap = new Heatmap();
        heatmap.setMatch(match);
        heatmap.setMap(match.getMap());
        heatmap.setType(type.toString());
        heatmap.setStatus(HeatmapEnums.ProcessState.CALCULATING.toString());

        return heatmap;
    }

    public Heatmap createForMap(String map, HeatmapEnums.Type type) {
        Heatmap heatmap = new Heatmap();
        heatmap.setMap(map);
        heatmap.setType(type.toString());
        heatmap.setStatus(HeatmapEnums.ProcessState.CALCULATING.toString());

        return heatmap;
    }
}
