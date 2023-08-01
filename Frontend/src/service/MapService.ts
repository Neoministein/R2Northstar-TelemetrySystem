import {AppConfig} from "../AppConfig";
import ErrorUtils from "../utils/ErrorUtils";

export interface GameMap {
    name: string
    type: string
    scale: MapScale
}

export interface MapScale {
    xOffset: number
    yOffset: number
    scale: number
}

export interface HeatmapEntity {
    data: any
    highestCount: number
    map: string
    type: string
    status: string
    pixelDensity: number
    description: string
}

export interface MapDistribution {
    map: string
    count: number
    percent: number
}

const MapService = {

    getAllMapDetails() : Promise<GameMap[]> {
        return fetch(AppConfig.apiUrl + "/map" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.hits;});
    },

    getMapDetails(mapId : string) : Promise<GameMap> {
        return fetch(AppConfig.apiUrl + "/map/" + mapId )
            .then(resp => { return ErrorUtils.parseResponse(resp);});
    },

    getMapHeatmap(map : string) : Promise<HeatmapEntity> {
        return fetch(AppConfig.apiUrl + "/map/" + map + "/heatmap")
            .then(resp => { return ErrorUtils.parseResponse(resp);});
    },

    getMapDistribution() : Promise<MapDistribution[]> {
        return fetch(AppConfig.apiUrl + "/map/distribution" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.hits;});
    }
}

export default MapService;
