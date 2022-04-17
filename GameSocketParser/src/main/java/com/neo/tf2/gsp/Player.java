package com.neo.tf2.gsp;

import org.json.JSONObject;

public class Player {

    private final String id;
    private int team;
    private boolean isTitan = false;
    private String titanClass;
    private String primary;
    private String  secondary;
    private String weapon3;

    private boolean isWallRunning;
    private boolean isShooting;

    private Vector position;
    private Vector rotation;
    private Vector velocity;
    private String health;

    public Player(String id, int team) {
        this.id = id;
        this.team = team;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("team",team);
        json.put("isTitan",isTitan);
        json.put("titanClass", titanClass);
        json.put("primary",primary);
        json.put("secondary",secondary);
        json.put("weapon3",weapon3);
        json.put("isWallRunning",isWallRunning);
        json.put("isShooting",isShooting);
        json.put("position",position.toJSON());
        json.put("rotation",rotation.toJSON());
        json.put("velocity",velocity.toJSON());
        json.put("health",health);
        return json;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setTitan(boolean titan) {
        isTitan = titan;
    }

    public void setTitanClass(String titanClass) {
        this.titanClass = titanClass;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public void setWeapon3(String weapon3) {
        this.weapon3 = weapon3;
    }

    public void setWallRunning(boolean wallRunning) {
        isWallRunning = wallRunning;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setRotation(Vector rotation) {
        this.rotation = rotation;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setHealth(String health) {
        this.health = health;
    }
}
