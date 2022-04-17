package com.neo.tf2.gsp.event;

import org.json.JSONObject;

public interface Event {

    EventType type();

    JSONObject toJson();
}
