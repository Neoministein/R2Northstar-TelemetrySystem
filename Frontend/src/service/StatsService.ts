import {AppConfig} from "../AppConfig";

export interface PlayerBucket {
    key: string
    playerName: string
}

export interface PlayerKillBucket extends PlayerBucket {
    PGS_PILOT_KILLS: number
}

export interface PlayerKdBucket extends PlayerBucket {
    kd: number
    PGS_PILOT_KILLS: number
    PGS_DEATHS: number
}

export interface NpcKillBucket extends PlayerBucket {
    PGS_NPC_KILLS: number
}

export interface WinRatioBucket extends PlayerBucket {
    ratio: number
    filters: WinRatioFilter
}

export interface WinRatioFilter {
    lose: number
    win: number
}

export interface WinsBucket extends PlayerBucket {
    win: number
}

export interface MatchResult {
    uId: string
    playerName: string
    PGS_ELIMINATED: number
    PGS_KILLS: number
    PGS_DEATHS: number
    PGS_PILOT_KILLS: number
    PGS_TITAN_KILLS: number
    PGS_NPC_KILLS: number
    PGS_ASSISTS: number
    PGS_SCORE: number
    PGS_ASSAULT_SCORE: number
    PGS_DEFENSE_SCORE: number
    PGS_DISTANCE_SCORE: number
    PGS_DETONATION_SCORE: number
}

const StatsService = {

    getMatchResult(matchId: string) : Promise<MatchResult[]> {
        return fetch(AppConfig.apiUrl + "/result/match/" + matchId).then(response => response.json())
            .then(data => data.hits);
    },

    getTopPlayerKills(tags : string[] = []) : Promise<PlayerKillBucket[]> {
        return fetch(AppConfig.apiUrl + "/result/top/player-kills?max=1000" + this.formatTagParam(tags)).then(response => response.json())
        .then(data => data.buckets);
    },

    getTopPlayerKd(tags : string[] = []) : Promise<PlayerKdBucket[]> {
        return fetch(AppConfig.apiUrl + "/result/top/player-kd?max=1000" + this.formatTagParam(tags)).then(response => response.json())
        .then(data => data.buckets);
    },

    getTopNpcKills(tags : string[] = []) : Promise<NpcKillBucket[]> {
        return fetch(AppConfig.apiUrl + "/result/top/npc-kills?max=1000" + this.formatTagParam(tags)).then(response => response.json())
        .then(data => data.buckets);
    },

    getTopWinRatio(tags : string[] = []) : Promise<WinRatioBucket[]> {
        return fetch(AppConfig.apiUrl + "/result/top/win-ratio?max=1000" + this.formatTagParam(tags)).then(response => response.json())
        .then(data => data.buckets);
    },

    getTopWins(tags : string[] = []) : Promise<WinsBucket[]> {
        return fetch(AppConfig.apiUrl + "/result/top/win?max=1000" + this.formatTagParam(tags)).then(response => response.json())
        .then(data => data.buckets);
    },


    formatTagParam(tags : string[]) : string {
        if(tags.length === 0) {
            return "";
        }

        return "&tags=" + tags.join(",");
    }
}

export default StatsService;
