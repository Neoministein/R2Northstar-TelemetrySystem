package com.neo.r2.ts.impl.map.heatmap;

import com.neo.r2.ts.impl.persistence.entity.HeatmapType;

import java.io.Serializable;

public class QueueableHeatmapInstruction implements Serializable {

    public static final String QUEUE_MESSAGE_TYPE = "QueueableHeatmapInstruction";

    private HeatmapType type;
    private String map;
    private String matchId;

    public QueueableHeatmapInstruction() {
        //Jackson requires empty constructor
    }


    public HeatmapType getType() {
        return type;
    }

    public void setType(HeatmapType type) {
        this.type = type;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
