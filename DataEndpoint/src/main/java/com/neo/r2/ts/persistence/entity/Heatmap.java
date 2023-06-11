package com.neo.r2.ts.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.mapper.JsonNodeStringJavaDescriptor;
import com.neo.util.common.api.json.Views;

import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import com.neo.util.framework.database.impl.AuditableDataBaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = Heatmap.TABLE_NAME)
public class Heatmap extends AuditableDataBaseEntity implements PersistenceEntity {

    public static final String TABLE_NAME = "HEATMAP";
    public static final String C_DATA = "DATA";
    public static final String C_HIGHEST_COUNT = "HIGHEST_COUNT";
    public static final String C_MAP = "MAP";
    public static final String C_TYPE = "TYPE";
    public static final String C_DESCRIPTION = "DESCRIPTION";

    @Id
    @Column(name = PersistenceEntity.C_ID, columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(Views.Public.class)
    private Long id;

    @Convert(converter = JsonNodeStringJavaDescriptor.class)
    @Column(name = C_DATA, nullable = false, columnDefinition = "text")
        @JsonView(Views.Public.class)
    private JsonNode data;

    @Column(name = C_HIGHEST_COUNT, nullable = false)
        @JsonView(Views.Public.class)
    private Long highestCount = 0L;

    @Column(name = C_MAP, nullable = false)
        @JsonView(Views.Public.class)
    private String map;

    @Column(name = C_TYPE, nullable = false)
        @JsonView(Views.Public.class)
    private String type;

    @Column(name = C_DESCRIPTION, nullable = false)
        @JsonView(Views.Public.class)
    private String description;

    @ManyToOne
        @JsonIgnore
    private Match match;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public Long getHighestCount() {
        return highestCount;
    }

    public void setHighestCount(Long highestCount) {
        this.highestCount = highestCount;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public Object getPrimaryKey() {
        return getId();
    }
}
