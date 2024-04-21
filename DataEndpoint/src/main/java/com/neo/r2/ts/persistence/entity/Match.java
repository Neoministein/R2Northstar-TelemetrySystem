package com.neo.r2.ts.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neo.util.common.api.json.Views;
import com.neo.util.framework.api.persistence.entity.PersistenceEntity;
import com.neo.util.framework.database.persistence.AuditableDataBaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "MATCH")
public class Match extends AuditableDataBaseEntity implements PersistenceEntity {

    public static final String TABLE_NAME = "Match";
    public static final String C_IS_RUNNING = "isRunning";
    public static final String C_NS_SERVER_NAME = "nsServerName";
    public static final String C_MAP = "map";
    public static final String C_GAMEMODE = "gamemode";
    public static final String C_MAX_PLAYERS = "MAX_PLAYERS";
    public static final String C_START_DATE = "startDate";
    public static final String C_MILLI_SEC_BETWEEN_STATE = "milliSecBetweenState";
    public static final String C_RECORD_NPC = "recordNpc";

    @Id
    @GeneratedValue
    @Column(name = "ID")
        @JsonView(Views.Public.class)
    private UUID id;

    @Column(name = "IS_RUNNING", nullable = false)
        @JsonView(Views.Public.class)
    private boolean isRunning = true;

    @Column(name = "NS_SERVER_NAME", nullable = false)
        @JsonView(Views.Public.class)
    private String nsServerName;

    @Column(name = "MAP", nullable = false)
        @JsonView(Views.Public.class)
    private String map;

    @Column(name = "GAMEMODE", nullable = false)
        @JsonView(Views.Public.class)
    private String gamemode;

    @Column(name = "MAX_PLAYERS", nullable = false)
        @JsonView(Views.Public.class)
    private Integer maxPlayers;

    @Column(name = "START_DATE", nullable = false, updatable = false)
        @JsonView(Views.Public.class)
    private Instant startDate = Instant.now();

    @Column(name = "END_DATE")
        @JsonView(Views.Public.class)
    private Instant endDate;

    @Column(name = "MILLI_SEC_BETWEEN_STATE", nullable = false)
        @JsonView(Views.Public.class)
    private Integer milliSecBetweenState;

    @Column(name = "RECORD_NPC", nullable = false)
        @JsonView(Views.Public.class)
    private boolean recordNpc;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "MATCH_TAG", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG")
        @JsonView(Views.Public.class)
    private List<String> tags = new ArrayList<>();

    @ManyToOne(optional = false)
        @JsonIgnore
    private ApplicationUser creator;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "match")
        @JsonIgnore
    private List<Heatmap> heatmaps = new ArrayList<>();

    public Match(String nsServerName, String map, String gamemode, int maxPlayers, int milliSecBetweenState, ApplicationUser creator, List<String> tags) {
        this.nsServerName = nsServerName;
        this.map = map;
        this.gamemode = gamemode;
        this.maxPlayers = maxPlayers;
        this.creator = creator;
        this.tags = tags;
        this.milliSecBetweenState = milliSecBetweenState;
    }

    protected Match() {
        //Required by JPA
    }


    public UUID getId() {
        return id;
    }

    public String getStringId() {
        return id.toString();
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

    public ApplicationUser getCreator() {
        return creator;
    }

    public void setCreator(ApplicationUser owner) {
        this.creator = owner;
    }

    public List<Heatmap> getHeatmaps() {
        return heatmaps;
    }

    public void setHeatmaps(List<Heatmap> heatmaps) {
        this.heatmaps = heatmaps;
    }

    public Integer getMilliSecBetweenState() {
        return milliSecBetweenState;
    }

    public void setMilliSecBetweenState(Integer milliSecBetweenState) {
        this.milliSecBetweenState = milliSecBetweenState;
    }

    public boolean isRecordNpc() {
        return recordNpc;
    }

    public void setRecordNpc(boolean recordNpc) {
        this.recordNpc = recordNpc;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @Override
    @JsonIgnore
    public Object getPrimaryKey() {
        return getId();
    }
}
