package com.neo.r2.ts.impl.map.scaling;

public class GameMap {

    enum MapType {
        MP, MP_LF, SP
    }

    private final String name;
    private final MapType type;
    private final MapScale mapScale;


    public GameMap(String name, MapType type, MapScale mapScale) {
        this.name = name;
        this.mapScale = mapScale;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public MapType getType() {
        return type;
    }

    public MapScale getMapScale() {
        return mapScale;
    }
}
