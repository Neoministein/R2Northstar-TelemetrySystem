package com.neo.r2.ts.impl.map.scaling;

import com.neo.r2.ts.api.CustomConstants;
import com.neo.util.common.impl.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class MapService {

    protected static final Map<String, GameMap> GAME_MAPS = new HashMap<>();

    @PostConstruct
    public void init() {
        GAME_MAPS.put("mp_angel_city",          new GameMap("mp_angel_city",        "Angel City",       GameMap.MapType.MP, new MapScale(6516,6633, 11.353)));
        GAME_MAPS.put("mp_black_water_canal",   new GameMap("mp_black_water_canal", "Black Water Canal",GameMap.MapType.MP, new MapScale(5416,6437,12)));
        GAME_MAPS.put("mp_coliseum",            new GameMap("mp_coliseum",          "Coliseum",         GameMap.MapType.MP,new MapScale(1510,1561,3)));
        GAME_MAPS.put("mp_coliseum_column",     new GameMap("mp_coliseum_column",   "Coliseum Column",  GameMap.MapType.MP, new MapScale(1510,1561,3)));
        GAME_MAPS.put("mp_colony02",            new GameMap("mp_colony02",          "Colony",           GameMap.MapType.MP, new MapScale(9504,8961,13)));
        GAME_MAPS.put("mp_complex3",            new GameMap("mp_complex3",          "Complex",          GameMap.MapType.MP, new MapScale(10855,3451,12)));
        GAME_MAPS.put("mp_crashsite3",          new GameMap("mp_crashsite3",        "Crashsite",        GameMap.MapType.MP, new MapScale(10668,4576,12)));
        GAME_MAPS.put("mp_drydock",             new GameMap("mp_drydock",           "Drydock",          GameMap.MapType.MP, new MapScale(5659,574910,3)));
        GAME_MAPS.put("mp_eden",                new GameMap("mp_eden",              "Eden",             GameMap.MapType.MP, new MapScale(4705,6163,11)));
        GAME_MAPS.put("mp_forwardbase_kodai",   new GameMap("mp_forwardbase_kodai", "Forwardbase Kodai",GameMap.MapType.MP, new MapScale(5272,5671,10)));
        GAME_MAPS.put("mp_glitch",              new GameMap("mp_glitch",            "Glitch",           GameMap.MapType.MP,new MapScale(8386,8336,16)));
        GAME_MAPS.put("mp_grave",               new GameMap("mp_grave",             "Boom Town",        GameMap.MapType.MP,new MapScale(2690,3866,14)));
        GAME_MAPS.put("mp_homestead",           new GameMap("mp_homestead",         "Homestead",        GameMap.MapType.MP,new MapScale(4839,5871,13)));
        GAME_MAPS.put("mp_lf_deck",             new GameMap("mp_lf_deck",           "Deck",             GameMap.MapType.MP_LF,new MapScale(2344,2780,5)));
        GAME_MAPS.put("mp_lf_meadow",           new GameMap("mp_lf_meadow",         "Meadow",           GameMap.MapType.MP_LF,new MapScale(3150,2699,6.4)));
        GAME_MAPS.put("mp_lf_stacks",           new GameMap("mp_lf_stacks",         "Stacks",           GameMap.MapType.MP_LF,new MapScale(2953,2699,6.3)));
        GAME_MAPS.put("mp_lf_township",         new GameMap("mp_lf_township",       "Township",         GameMap.MapType.MP_LF,new MapScale(2317,2766,5)));
        GAME_MAPS.put("mp_lf_traffic",          new GameMap("mp_lf_traffic",        "Traffic",          GameMap.MapType.MP_LF,new MapScale(2216,2788,5)));
        GAME_MAPS.put("mp_lf_uma",              new GameMap("mp_lf_uma",            "Uma",              GameMap.MapType.MP_LF,new MapScale(2216,2788,5)));
        GAME_MAPS.put("mp_relic02",             new GameMap("mp_relic02",           "Relic",            GameMap.MapType.MP,new MapScale(7428,2375,15)));
        GAME_MAPS.put("mp_rise",                new GameMap("mp_rise",              "Rise",             GameMap.MapType.MP,new MapScale(7558,7399,12)));
        GAME_MAPS.put("mp_thaw",                new GameMap("mp_thaw",              "Exoplanet",        GameMap.MapType.MP,new MapScale(5046,5515,11.188)));
        GAME_MAPS.put("mp_wargames",            new GameMap("mp_wargames",          "Wargames",         GameMap.MapType.MP, new MapScale(5923,5105,9.5)));
    }

    public Optional<GameMap> fetchMap(String map) {
        return Optional.ofNullable(GAME_MAPS.get(map));
    }

    public GameMap requestMap(String map) {
        return fetchMap(map).orElseThrow(() ->
                new ValidationException(CustomConstants.EX_UNSUPPORTED_MAP, map));
    }

    public Optional<MapScale> fetchMapScale(String map) {
        return fetchMap(map).map(GameMap::scale);
    }

    public List<GameMap> fetchAll() {
       return new ArrayList<>(GAME_MAPS.values());
    }

}
