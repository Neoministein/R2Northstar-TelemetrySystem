package com.neo.r2.ts.impl.rest.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neo.r2.ts.impl.persistence.entity.Match;

public record MatchDto(@JsonUnwrapped Match match, int numberOfPlayers) {
}
