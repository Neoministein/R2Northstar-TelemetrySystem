package com.neo.r2.ts.impl.rest.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.util.framework.elastic.api.ElasticMappingConstants;
import com.neo.util.framework.rest.api.parser.InboundDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

@InboundDto
public record MatchResultDto(

        @Size(max = ElasticMappingConstants.KEYWORD)
        String matchId,

        @Size(max = ElasticMappingConstants.KEYWORD)
        @JsonProperty(required = true)
        String map,

        @Size(max = ElasticMappingConstants.KEYWORD)
        @JsonProperty(required = true)
        String gamemode,

        @JsonProperty(required = true)
        @Size(max = 256)
        List<Player> players
){

    public record Player(
            @JsonProperty(required = true)
            @Size(max = ElasticMappingConstants.KEYWORD)
            String uId,

            @JsonProperty(required = true)
            @Size(max = ElasticMappingConstants.KEYWORD)
            String playerName,

            @JsonProperty(required = true)
            boolean hasWon,

            @JsonProperty(value = "PGS_ELIMINATED")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsEliminated,

            @JsonProperty(value = "PGS_KILLS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsKills,

            @JsonProperty(value = "PGS_DEATHS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsDeaths,

            @JsonProperty(value = "PGS_PILOT_KILLS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsPilotKills,

            @JsonProperty(value = "PGS_TITAN_KILLS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsTitanKills,

            @JsonProperty(value = "PGS_NPC_KILLS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsNpcKills,

            @JsonProperty(value = "PGS_ASSISTS")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsAssists,

            @JsonProperty(value = "PGS_SCORE")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsScore,

            @JsonProperty(value = "PGS_ASSAULT_SCORE")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsAssaultScore,

            @JsonProperty(value = "PGS_DEFENSE_SCORE")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsDefenseScore,

            @JsonProperty(value = "PGS_DISTANCE_SCORE")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsDistanceScore,


            @JsonProperty(value = "PGS_DETONATION_SCORE")
            @Min(value = ElasticMappingConstants.INT_MIN)
            @Max(value = ElasticMappingConstants.INT_MAX)
            Integer pgsDetonationScore
    ){}
}
