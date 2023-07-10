package com.neo.r2.ts.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.mapper.JsonNodeStringJavaDescriptor;
import com.neo.util.common.api.json.Views;
import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import com.neo.util.framework.database.persistence.AuditableDataBaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "HEATMAP")
public class Heatmap extends AuditableDataBaseEntity implements PersistenceEntity {

    public static final String TABLE_NAME = "Heatmap";
    public static final String C_DATA = "data";
    public static final String C_HIGHEST_COUNT = "highestCount";
    public static final String C_MAP = "map";
    public static final String C_TYPE = "type";
    public static final String C_DESCRIPTION = "description";

    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(Views.Public.class)
    private Long id;

    @Convert(converter = JsonNodeStringJavaDescriptor.class)
    @Column(name = "DATA", nullable = false, columnDefinition = "text")
        @JsonView(Views.Public.class)
    private JsonNode data;

    @Column(name = "HIGHEST_COUNT", nullable = false)
        @JsonView(Views.Public.class)
    private Long highestCount = 0L;

    @Column(name = "MAP", nullable = false)
        @JsonView(Views.Public.class)
    private String map;

    @Column(name = "TYPE", nullable = false)
        @JsonView(Views.Public.class)
    private String type;

    @Column(name = "DESCRIPTION", nullable = false)
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
