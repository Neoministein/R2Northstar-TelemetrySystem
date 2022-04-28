package com.neo.tf2.ms.impl.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.neo.common.api.json.Views;
import com.neo.javax.api.persitence.entity.DataBaseEntity;
import com.neo.javax.impl.persistence.entity.AbstractDataBaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = Match.TABLE_NAME)
public class Match extends AbstractDataBaseEntity implements DataBaseEntity {

    public static final String TABLE_NAME = "match";
    public static final String C_IS_PLAYING = "is_running";
    public static final String C_NS_SERVER_NAME = "ns_server_name";
    public static final String C_GAMEMODE = "gamemode";
    public static final String C_MAP = "map";

    @Id
    @Type(type = "uuid-char")
    @Column(name = DataBaseEntity.C_ID)
        @JsonView(Views.Public.class)
    private UUID id = UUID.randomUUID();

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
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

    @Override
    @JsonIgnore
    public Object getPrimaryKey() {
        return getId();
    }
}
