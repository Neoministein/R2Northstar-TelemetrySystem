package com.neo.r2.ts.impl.rest.dto;

import com.neo.r2.ts.persistence.entity.Match;
import com.neo.util.framework.api.FrameworkMapping;
import com.neo.util.framework.database.api.DatabaseMappingConstants;
import com.neo.util.framework.rest.api.parser.InboundDto;
import jakarta.validation.constraints.Size;

@InboundDto
public record GenericMatchDto(
        @Size(max = FrameworkMapping.UUID)
        String id,
        boolean isRunning,
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String nsServerName,
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String map,
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String gamemode,
        @Size(max = DatabaseMappingConstants.VARCHAR)
        Integer maxPlayers,
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String createdBy) {

    public GenericMatchDto(Match match) {
        this(match.getId().toString(), match.getIsRunning(), match.getNsServerName(), match.getMap(), match.getGamemode(), match.getMaxPlayers(), match.getCreatedBy());
    }
}
