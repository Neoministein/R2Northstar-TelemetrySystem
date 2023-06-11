package com.neo.r2.ts.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neo.util.common.api.json.Views;

import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import com.neo.util.framework.database.impl.AuditableDataBaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = Match.TABLE_NAME)
public class Match extends AuditableDataBaseEntity implements PersistenceEntity {
    public static final String TABLE_NAME = "MATCH";
    public static final String C_IS_PLAYING = "IS_RUNNING";
    public static final String C_NS_SERVER_NAME = "NS_SERVER_NAME";
    public static final String C_GAMEMODE = "GAMEMODE";
    public static final String C_MAP = "MAP";
    public static final String C_START_DATE = "START_DATE";
    public static final String C_MAX_PLAYERS = "MAX_PLAYERS";

    @Id
    @GeneratedValue
    @Column(name = PersistenceEntity.C_ID)
        @JsonView(Views.Public.class)
    private UUID id;

    @Column(name = C_IS_PLAYING, nullable = false)
        @JsonView(Views.Public.class)
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

    @Column(name = C_MAX_PLAYERS, nullable = false)
        @JsonView(Views.Public.class)
    private Integer maxPlayers;

    @Column(name = C_START_DATE, nullable = false, updatable = false)
        @JsonView(Views.Public.class)
    private Instant startDate = Instant.now();

    @ManyToOne
        @JsonIgnore
    private ApplicationUser user;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
        @JsonIgnore
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

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser owner) {
        this.user = owner;
    }

    public List<Heatmap> getHeatmaps() {
        return heatmaps;
    }

    public void setHeatmaps(List<Heatmap> heatmaps) {
        this.heatmaps = heatmaps;
    }

    @Override
    @JsonIgnore
    public Object getPrimaryKey() {
        return getId();
    }
}
