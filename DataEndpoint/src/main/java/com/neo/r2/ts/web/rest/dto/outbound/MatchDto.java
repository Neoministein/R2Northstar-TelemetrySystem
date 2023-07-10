package com.neo.r2.ts.web.rest.dto.outbound;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neo.r2.ts.persistence.entity.Match;

public record MatchDto(@JsonUnwrapped Match match, int numberOfPlayers) {
}
