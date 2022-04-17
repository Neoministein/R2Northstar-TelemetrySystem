package com.neo.tf2.gsp;

import java.io.Serializable;
import java.util.Date;

public class PlayerGameState implements Serializable {

    private final Date recordDate;

    private final Player player;

    private final Vector position;
    private final Vector rotation;
    private final Vector velocity;
    private final String health;

    public PlayerGameState(Date recordDate, Player player, Vector position, Vector rotation, Vector velocity, String health) {
        this.recordDate = recordDate;
        this.player = player;
        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
        this.health = health;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public Player getPlayer() {
        return player;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getRotation() {
        return rotation;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public String getHealth() {
        return health;
    }
}
