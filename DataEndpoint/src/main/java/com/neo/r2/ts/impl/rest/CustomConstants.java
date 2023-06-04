package com.neo.r2.ts.impl.rest;

import com.neo.util.common.impl.exception.ExceptionDetails;

public class CustomConstants {

    public static final ExceptionDetails EX_SERVICE_UNAVAILABLE = new ExceptionDetails(
            "svc", "Internal service not available", false
    );

    public static final ExceptionDetails EX_UNSUPPORTED_MAP = new ExceptionDetails(
            "r2ts/map/unsupported", "The provided map {0} does not exits or isn't supported.", false
    );

    public static final ExceptionDetails EX_NO_HEATMAP_FOR_MAP = new ExceptionDetails(
            "r2ts/map/no-heatmap", "No Heatmap exists for the map {0}.", false
    );

    public static final ExceptionDetails EX_MATCH_NON_EXISTENT = new ExceptionDetails(
            "r2ts/match/no-match", "The provided match {0} does not exist.", false
    );

    public static final ExceptionDetails EX_ALREADY_MATCH_ENDED = new ExceptionDetails(
            "r2ts/match/already-ended", "The provided match {0} has already ended.", false
    );

    public static final ExceptionDetails EX_NO_HEATMAP_FOR_MATCH = new ExceptionDetails(
            "r2ts/map/no-heatmap", "No Heatmap exists for the match {0}.", false
    );

    public static final ExceptionDetails EX_PLAYER_FOUND = new ExceptionDetails(
            "r2ts/result/no-player", "No player found with identifier {0}.", false
    );

    private CustomConstants() {}
}
