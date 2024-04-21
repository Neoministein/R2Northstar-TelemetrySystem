package com.neo.r2.ts.impl.match;

public record MatchStatusEvent(MatchStatusEvent.Type type, String matchId) {

    public enum Type {
        CREATED,
        ENDED
    }

}
