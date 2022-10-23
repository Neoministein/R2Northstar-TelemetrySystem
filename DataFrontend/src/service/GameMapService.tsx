import {AppConfig} from "../Config";

export interface GameMapEntity {
    name: string
    type: string
    scale: GameMapScaleEntity
}

export interface GameMapScaleEntity {
    xOffset: number
    yOffset: number
    scale: number
}

export interface HeatmapEntity {
    data: any
    highestCount: number
    map: string
    type: string
    description: string
}

export class GameMapService {

    getMap(map : string) : Promise<GameMapEntity> {
        return fetch(AppConfig.apiUrl + "/map/" + map ).then(response => response.json());
    }

    getMapScale(map : string) : Promise<GameMapScaleEntity> {
        return fetch(AppConfig.apiUrl + "/map/" + map + "/scale").then(response => response.json());
    }

    getMapHeatmap(map : string) : Promise<HeatmapEntity> {
        return fetch(AppConfig.apiUrl + "/map/" + map + "/heatmap").then(response => response.json());
    }

    getAllMaps() : Promise<GameMapEntity[]> {
        return fetch(AppConfig.apiUrl + "/map").then(response => response.json()).then(d => d.hits);
    }
}