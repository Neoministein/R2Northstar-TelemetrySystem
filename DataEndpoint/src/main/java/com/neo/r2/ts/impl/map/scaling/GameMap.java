package com.neo.r2.ts.impl.map.scaling;

public record GameMap(String name, com.neo.r2.ts.impl.map.scaling.GameMap.MapType type, MapScale scale) {

    enum MapType {
        MP, MP_LF, SP
    }

}
