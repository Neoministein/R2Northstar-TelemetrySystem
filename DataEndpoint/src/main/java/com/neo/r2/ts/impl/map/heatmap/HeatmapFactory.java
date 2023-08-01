package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.persistence.HeatmapEnums;
import com.neo.r2.ts.persistence.entity.Heatmap;
import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.framework.api.config.ConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HeatmapFactory {

    protected static final int DEFAULT_PIXEL_DENSITY = 4;

    @Inject
    protected ConfigService configService;

    public Heatmap createForMatch(Match match, HeatmapEnums.Type type) {
        Heatmap heatmap = new Heatmap();
        heatmap.setMatch(match);
        heatmap.setMap(match.getMap());
        heatmap.setType(type.toString());
        heatmap.setPixelDensity(configService.get("r2ts.heatmap.pixelDensity").asInt().orElse(DEFAULT_PIXEL_DENSITY));
        heatmap.setStatus(HeatmapEnums.ProcessState.CALCULATING.toString());

        return heatmap;
    }

    public Heatmap createForMap(String map, HeatmapEnums.Type type) {
        Heatmap heatmap = new Heatmap();
        heatmap.setMap(map);
        heatmap.setType(type.toString());
        heatmap.setPixelDensity(configService.get("r2ts.heatmap.pixelDensity").asInt().orElse(DEFAULT_PIXEL_DENSITY));
        heatmap.setStatus(HeatmapEnums.ProcessState.CALCULATING.toString());

        return heatmap;
    }
}
