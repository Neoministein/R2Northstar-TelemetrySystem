package com.neo.r2.ts.impl.match.result;

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

    public List<Distribution> getMapDistribution() {
        List<Distribution> list = new ArrayList<>();

        int numberOfMatches = matchResultRepository.countMatches();
        for (GameMap map: mapService.fetchAll()) {
            int count = matchResultRepository.countMatchesByMap(map.name());

            list.add(new Distribution(map.name(), count, 100F / numberOfMatches * count));

        }

        return list;
    }

    public List<Distribution> getGamemodeDistribution() {
        List<Distribution> list = new ArrayList<>();

        int numberOfMatches = matchResultRepository.countMatches();
        for (String type: matchResultRepository.fetchAllGamemodeTypes()) {
            int count = matchResultRepository.countMatchesByGamemode(type);

            list.add(new Distribution(type, count, 100F / numberOfMatches * count));

        }

        return list;
    }

    public record Distribution(String name, int count, float percent){}
}
