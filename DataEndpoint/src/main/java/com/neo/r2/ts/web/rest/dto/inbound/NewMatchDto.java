package com.neo.r2.ts.web.rest.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.util.framework.database.api.DatabaseMappingConstants;
import com.neo.util.framework.rest.api.parser.InboundDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@InboundDto
public record NewMatchDto(
        @JsonProperty(required = true)
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String nsServerName,

        @JsonProperty(required = true)
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String map,

        @JsonProperty(required = true)
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String gamemode,

        @JsonProperty(required = true)
        @Min(0)
        @Max(256)
        int maxPlayers,

        @JsonProperty(required = true)
        @Min(100)
        @Max(5_000)
        int milliSecBetweenState,

        @JsonProperty(required = true)
        boolean recordNpc,

        @JsonProperty(required = true)
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String tags) {}
