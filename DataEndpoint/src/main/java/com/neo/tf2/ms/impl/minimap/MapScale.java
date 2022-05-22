package com.neo.tf2.ms.impl.minimap;

public class MapScale {

    private final String map;
    private final long posX;
    private final long posY;
    private final double scale;

    public MapScale(String map, long posX, long posY, double scale) {
        this.map = map;
        this.posX = posX;
        this.posY = posY;
        this.scale = scale;
    }

    public String getMap() {
        return map;
    }

    public long getPosX() {
        return posX;
    }

    public long getPosY() {
        return posY;
    }

    public double getScale() {
        return scale;
    }

    public long toMinimapScaleX(long gameX) {
        return Math.round((gameX + posX) / scale);
    }

    public long toMinimapScaleY(long gameY) {
        return Math.round((gameY + posY) / scale);
    }

    public long toGameScaleX(long minimapX) {
        return Math.round(minimapX * scale - posX);
    }

    public long toGameScaleY(long minimapY) {
        return Math.round(minimapY * scale - posX);
    }

    public long toMinimapFormatX(long gameX) {
        return Math.round((gameX + posX) / scale);
    }

    public long toMinimapFormatY(long gameY) {
        return Math.round((gameY * -1 + posY) / scale);
    }
}
