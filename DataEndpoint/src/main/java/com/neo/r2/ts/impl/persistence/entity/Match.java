package com.neo.r2.ts.impl.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neo.util.common.api.json.Views;
import com.neo.util.framework.api.persistence.entity.DataBaseEntity;
import com.neo.util.framework.persistence.impl.AuditableDataBaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = Match.TABLE_NAME)
public class Match extends AuditableDataBaseEntity implements DataBaseEntity {

    public static final String TABLE_NAME = "match";
    public static final String C_IS_PLAYING = "isRunning";
    public static final String C_NS_SERVER_NAME = "nsServerName";
    public static final String C_GAMEMODE = "gamemode";
    public static final String C_MAP = "map";
    public static final String C_START_DATE = "startDate";
    public static final String C_OWNER = "owner";

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    @Column(name = DataBaseEntity.C_ID)
        @JsonView(Views.Public.class)
    private UUID id;

    @Column(name = C_IS_PLAYING, nullable = false)
        @JsonView(Views.Internal.class)
    private boolean isRunning = true;

    @Column(name = C_NS_SERVER_NAME, nullable = false)
        @JsonView(Views.Public.class)
    private String nsServerName;

    @Column(name = C_MAP, nullable = false)
        @JsonView(Views.Public.class)
    private String map;

    @Column(name = C_GAMEMODE, nullable = false)
        @JsonView(Views.Public.class)
    private String gamemode;

    @Column(name = C_START_DATE, nullable = false, updatable = false)
        @JsonView(Views.Public.class)
    private Date startDate = new Date();

    @Column(name = C_OWNER)
        @JsonView(Views.Owner.class)
    private UUID owner;

    @OneToMany(mappedBy = TABLE_NAME, orphanRemoval = true)
        @JsonView(Views.Public.class)
    private List<Heatmap> heatmaps = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean running) {
        isRunning = running;
    }

    public String getNsServerName() {
        return nsServerName;
    }

    public void setNsServerName(String nsServerName) {
        this.nsServerName = nsServerName;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setOwner(UUID matchOwner) {
        this.owner = matchOwner;
    }

    @Override
    @JsonIgnore
    public Object getPrimaryKey() {
        return getId();
    }
}
