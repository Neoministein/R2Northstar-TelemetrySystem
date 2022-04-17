package com.neo.tf2.gsp;

import org.json.JSONObject;

import java.io.Serializable;

public class Vector implements Serializable {

    private final float x;
    private final float y;
    private final float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("x",x);
        json.put("y",x);
        json.put("z",x);
        return json;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
