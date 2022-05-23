package com.neo.tf2.ms.impl.map.scaling;

import com.neo.common.impl.exception.InternalLogicException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class MapScalingService {

    protected static final Map<String, MapScale> MAP_SCALE_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
        MAP_SCALE_MAP.put("mp_angel_city",          new MapScale("mp_angel_city", 6516,6633, 11.353));
        MAP_SCALE_MAP.put("mp_black_water_canal",   new MapScale("mp_black_water_canal",5416,6437,12));
        MAP_SCALE_MAP.put("mp_coliseum",            new MapScale("mp_coliseum",1510,1561,3));
        MAP_SCALE_MAP.put("mp_coliseum_column",     new MapScale("mp_coliseum_column",1510,1561,3));
        MAP_SCALE_MAP.put("mp_colony02",            new MapScale("mp_colony02",9504,8961,13));
        MAP_SCALE_MAP.put("mp_complex3",            new MapScale("mp_complex3",10855,3451,12));
        MAP_SCALE_MAP.put("mp_crashsite3",          new MapScale("mp_crashsite3",10668,4576,12));
        MAP_SCALE_MAP.put("mp_drydock",             new MapScale("mp_drydock",5659,574910,3));
        MAP_SCALE_MAP.put("mp_eden",                new MapScale("mp_eden",4705,6163,11));
        MAP_SCALE_MAP.put("mp_forwardbase_kodai",   new MapScale("mp_forwardbase_kodai",5272,5671,10));
        MAP_SCALE_MAP.put("mp_glitch",              new MapScale("mp_glitch",8386,8336,16));
        MAP_SCALE_MAP.put("mp_grave",               new MapScale("mp_grave",2690,3866,14));
        MAP_SCALE_MAP.put("mp_homestead",           new MapScale("mp_homestead",4839,5871,13));
        MAP_SCALE_MAP.put("mp_lf_deck",             new MapScale("mp_lf_deck",2344,2780,5));
        MAP_SCALE_MAP.put("mp_lf_meadow",           new MapScale("mp_lf_meadow",3150,2699,6.4));
        MAP_SCALE_MAP.put("mp_lf_stacks",           new MapScale("mp_lf_stacks",2953,2699,6.3));
        MAP_SCALE_MAP.put("mp_lf_township",         new MapScale("mp_lf_township",2317,2766,5));
        MAP_SCALE_MAP.put("mp_lf_traffic",          new MapScale("mp_lf_traffic",2216,2788,5));
        MAP_SCALE_MAP.put("mp_lf_uma",              new MapScale("mp_lf_uma",2216,2788,5));
        MAP_SCALE_MAP.put("mp_relic02",             new MapScale("mp_relic02",7428,2375,15));
        MAP_SCALE_MAP.put("mp_rise",                new MapScale("mp_rise",7558,7399,12));
        MAP_SCALE_MAP.put("mp_thaw",                new MapScale("mp_thaw",5046,5515,11.188));
        MAP_SCALE_MAP.put("mp_wargames",            new MapScale("mp_wargames",5923,5105,9.5));
    }

    public MapScale getMapScale(String map) {
        MapScale mapScale = MAP_SCALE_MAP.get(map);
        if (mapScale != null) {
            return mapScale;
        }
        throw new InternalLogicException("Unsupported map");
    }

}
