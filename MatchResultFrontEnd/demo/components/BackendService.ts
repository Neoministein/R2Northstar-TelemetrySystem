import {AppConfig} from "../AppConfig";

export interface PlayerBucket {
    key: string
    playerName: string
}

export interface PlayerKillBucket extends PlayerBucket {
    PGS_PILOT_KILLS: number
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

export interface PlayerLookUp {
    uId: string
    playerName: string
}

export class BackendService {

    resolvePlayerName<T extends PlayerBucket>(promiseToResolve : Promise<T[]>) : Promise<T[]> {
        return promiseToResolve.then(data => {
            const promises = data.map(item => {
                return this.getPlayerNameFromUid(item.key).then(data => {
                    item.playerName = data.playerName;
                    return item;
                });
              });
            return Promise.all(promises);
        });
    }

    getTopPlayerKills() : Promise<PlayerKillBucket[]> {
        return this.resolvePlayerName(fetch(AppConfig.apiUrl + "/result/top/player-kills/").then(response => response.json())
        .then(data => data.buckets));
    }

    getTopNpcKills() : Promise<NpcKillBucket[]> {
        return this.resolvePlayerName(fetch(AppConfig.apiUrl + "/result/top/npc-kills/").then(response => response.json())
        .then(data => data.buckets));
    }

    getTopWinRatio() : Promise<WinRatioBucket[]> {
        return this.resolvePlayerName(fetch(AppConfig.apiUrl + "/result/top/win-ratio/").then(response => response.json())
        .then(data => data.buckets));
    }

    getPlayerNameFromUid(uid : string) : Promise<PlayerLookUp> {
        return fetch(AppConfig.apiUrl + "/result/player/uid/" + uid ).then(response => response.json());
    }
}