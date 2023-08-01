import {AppConfig} from "../AppConfig";
import MapService, {GameMap, HeatmapEntity} from "./MapService";
import ErrorUtils from "../utils/ErrorUtils";

export interface MatchEntity {
    id: string
    nsServerName: string
    map: string
    gamemode: string
    startDate: number
    maxPlayers: number
    numberOfPlayers: number
    mapDetails?: GameMap
}

const MatchService = {

    getMatch(matchId : string) : Promise<MatchEntity>{
        return fetch(AppConfig.apiUrl + "/match/" + matchId )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(async match => {
                match.mapDetails = await MapService.getMapDetails(match.map)
                return match;
            });
    },

    getRunningMatches() : Promise<MatchEntity[]> {
        return fetch(AppConfig.apiUrl + "/match/playing")
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(d => {return d.hits;});
    },

    getFinishedMatches() : Promise<MatchEntity[]> {
        return fetch(AppConfig.apiUrl + "/match/stopped")
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(d => {
                return d.hits;
            });
    },

    getHeatmap(matchId : string) : Promise<HeatmapEntity>{
        return fetch(AppConfig.apiUrl + "/match/" + matchId + "/heatmap")
            .then(resp => { return ErrorUtils.parseResponse(resp);});
    }
}

export default MatchService;
