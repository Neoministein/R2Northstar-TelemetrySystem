package com.neo.r2.ts.impl.rest.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.util.framework.rest.api.parser.InboundDto;

@InboundDto
public record NewMatchDto(
        @JsonProperty(value = "ns_server_name",required = true)
        String nsServerName,

        @JsonProperty(required = true)
        String map,

        @JsonProperty(required = true)
        String gamemode,

        @JsonProperty(required = true)
        int maxPlayers) {}
