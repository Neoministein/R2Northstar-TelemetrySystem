package com.neo.r2.ts.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.r2.ts.persistence.entity.ApplicationUser;
import com.neo.util.framework.database.api.DatabaseMappingConstants;
import com.neo.util.framework.rest.api.parser.InboundDto;
import jakarta.validation.constraints.Size;

import java.util.List;

@InboundDto
public record GenericUserDto(

        @Size(max = DatabaseMappingConstants.VARCHAR)
        String uId,

        @Size(max = DatabaseMappingConstants.VARCHAR)
        String apiKey,

        @JsonProperty(required = true)
        @Size(max = DatabaseMappingConstants.VARCHAR)
        String displayName,

        @Size(max = DatabaseMappingConstants.VARCHAR)
        String description,

        @JsonProperty(defaultValue = "false")
        @Size(max = DatabaseMappingConstants.VARCHAR)
        boolean disabled,

        @Size(max = 255)
        List<String> roles
) {

        public GenericUserDto(ApplicationUser applicationUser) {
                this(applicationUser.getUid().toString(),
                        applicationUser.getApiKey(),
                        applicationUser.getDisplayName(),
                        applicationUser.getDescription(),
                        applicationUser.isDisabled(),
                        applicationUser.getUserRoles());
        }
}
