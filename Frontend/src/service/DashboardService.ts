import {AppConfig} from "../AppConfig";
import ErrorUtils from "../utils/ErrorUtils";
import {Distribution} from "./MapService";

const DashboardService = {
    getModeDistribution() : Promise<Distribution[]> {
        return fetch(AppConfig.apiUrl + "/dashboard/mode/distribution" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.hits;});
    },

    getTotalUniquePlayers() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/unqiue/player" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },

    getTotalUniquePlayers24h() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/unqiue/player?timeunit=DAYS&time=1" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalPlayerKills24h() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/total/player-kills?timeunit=DAYS&time=1" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalNpcKills24h() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/total/npc-kills?timeunit=DAYS&time=1" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalMatches() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/matches" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalMatches24h() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/matches?timeunit=DAYS&time=1" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalDistance() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/total/player-distance" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    },
    getTotalDistance24h() : Promise<number> {
        return fetch(AppConfig.apiUrl + "/dashboard/total/player-distance?timeunit=DAYS&time=1" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.value;});
    }
}

export default DashboardService
