package com.neo.r2.ts.impl.map.scaling;

public record GameMap(String name, String displayName, MapType type, MapScale scale) {

    enum MapType {
        MP, MP_LF, SP
    }

}
