package com.neo.r2.ts.impl.result;

import com.neo.r2.ts.impl.map.scaling.GameMap;
import com.neo.r2.ts.impl.map.scaling.MapService;
import com.neo.r2.ts.impl.repository.searchable.MatchResultRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MatchResultService {

    @Inject
    protected MatchResultRepository matchResultRepository;

    @Inject
    protected MapService mapService;

    public List<MapDistribution> getMapDistribution() {
        List<MapDistribution> list = new ArrayList<>();


        int numberOfMatches = matchResultRepository.countMatches();
        for (GameMap map: mapService.fetchAll()) {
            int count = matchResultRepository.countMatchesByMap(map.name());

            list.add(new MapDistribution(map.name(), count, 100F / numberOfMatches * count));

        }

        return list;
    }

    public record MapDistribution(String map, int count, float percent){}


}
