package com.neo.r2.ts.impl.map.scaling;

public class MapScale {

    private final String map;
    private final long xOffset;
    private final long yOffset;
    private final double scale;

    public MapScale(String map, long posX, long posY, double scale) {
        this.map = map;
        this.xOffset = posX;
        this.yOffset = posY;
        this.scale = scale;
    }

    public String getMap() {
        return map;
    }

    public long getxOffset() {
        return xOffset;
    }

    public long getyOffset() {
        return yOffset;
    }

    public double getScale() {
        return scale;
    }

    public long toMinimapScaleX(long gameX) {
        return Math.round((gameX + xOffset) / scale);
    }

    public long toMinimapScaleY(long gameY) {
        return Math.round((gameY + yOffset) / scale);
    }

    public long toGameScaleX(long minimapX) {
        return Math.round(minimapX * scale - xOffset);
    }

    public long toGameScaleY(long minimapY) {
        return Math.round(minimapY * scale - xOffset);
    }

    public long toMinimapFormatX(long gameX) {
        return Math.round((gameX + xOffset) / scale);
    }

    public long toMinimapFormatY(long gameY) {
        return Math.round((gameY * -1 + yOffset) / scale);
    }
}
