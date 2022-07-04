package com.neo.r2.ts.impl.persistence.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.neo.r2.ts.impl.persistence.entity.mapper.JsonNodeStringType;
import com.neo.util.common.api.json.Views;
import com.neo.util.framework.api.persistence.entity.DataBaseEntity;
import com.neo.util.framework.persistence.impl.AuditableDataBaseEntity;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = Heatmap.TABLE_NAME)
@TypeDef(name = "jsonnode", typeClass = JsonNodeStringType.class)
public class Heatmap extends AuditableDataBaseEntity implements DataBaseEntity {

    public static final String TABLE_NAME = "heatmap";
    public static final String C_DATA = "data";
    public static final String C_HIGHEST_COUNT = "highestCount";
    public static final String C_MAP = "map";
    public static final String C_TYPE = "type";
    public static final String C_DESCRIPTION = "description";

    @Id
    @Column(name = DataBaseEntity.C_ID, columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonView(Views.Public.class)
    private Long id;

    @Type(type = "jsonnode")
    @Column(name = C_DATA, nullable = false, columnDefinition = "text")
        @JsonView(Views.Public.class)
    private JsonNode data;

    @Column(name = C_HIGHEST_COUNT, nullable = false)
        @JsonView(Views.Public.class)
    private Long highestCount;

    @Column(name = C_MAP, nullable = false)
        @JsonView(Views.Public.class)
    private String map;

    @Enumerated(EnumType.STRING)
    @Column(name = C_TYPE, nullable = false)
        @JsonView(Views.Public.class)
    private HeatmapType type;

    @Column(name = C_DESCRIPTION, nullable = false)
        @JsonView(Views.Public.class)
    private String description;

    @ManyToOne
        @JsonView(Views.Public.class)
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

    public HeatmapType getType() {
        return type;
    }

    public void setType(HeatmapType type) {
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
