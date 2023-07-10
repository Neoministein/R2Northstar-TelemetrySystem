package com.neo.r2.ts.impl.player;

import com.neo.util.framework.rest.api.parser.InboundDto;

@InboundDto
public record PlayerSearchLookUpDto(
        String[] playerUIds) {
}
