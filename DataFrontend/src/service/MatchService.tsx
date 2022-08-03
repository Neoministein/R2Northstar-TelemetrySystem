import {AppConfig} from "../Config";

export interface MatchEntity {
    id: string
    nsServerName: string
    map: string
    gamemode: string
    startDate: number
    heatmaps: []
}

export class MatchService {

    getMatch(id : string) : Promise<MatchEntity>{
        return fetch(AppConfig.apiUrl + "/match/" + id ).then(res => res.json()).then(d => d.data);
    }

    getRunningMatches() : Promise<MatchEntity[]> {
        return fetch(AppConfig.apiUrl + "/match/playing").then(res => res.json()).then(d => d.data.hits);
    }
}