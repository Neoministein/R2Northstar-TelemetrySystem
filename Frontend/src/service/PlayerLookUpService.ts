import {AppConfig} from "../AppConfig";

export interface PlayerLookUp {
    uId: string
    playerName: string
}

type CachedPlayerLookup = PlayerLookUp | Promise<PlayerLookUp>;

const PlayerLookUpService = {

    playerCache: new Map<string, CachedPlayerLookup>(),

    getPlayerNameFromUid(uid : string) : Promise<PlayerLookUp> {
        const cachedPlayer = this.playerCache.get(uid);
        if(cachedPlayer === undefined) {
            const promise = fetch(AppConfig.apiUrl + "/player/uid/" + uid ).then(response => response.json()).then(playerLookUp => {
                this.playerCache.set(uid, playerLookUp);
                return playerLookUp;
            });

            this.playerCache.set(uid, promise);

            return promise;
        } else if (cachedPlayer instanceof Promise) {
            return cachedPlayer;
        }

        return Promise.resolve(cachedPlayer);
    },

    async getPlayerNamesFromUid(uIds : string[]) : Promise<any> {
        if (uIds.length === 0) {
            return Promise.resolve([])
        }

        const uIdsToLookUp = [];
        const responseObject = {};

        uIds.forEach(uId => {
            const cachedPlayer = this.playerCache.get(uId);
            if (cachedPlayer === undefined || cachedPlayer instanceof Promise) {
                uIdsToLookUp.push(uId);
            } else {
                responseObject[cachedPlayer.uId] = cachedPlayer.playerName;
            }
        })


        const lookUpObject = await fetch(AppConfig.apiUrl + "/player/uid/search", {
            method: "POST",
            body: JSON.stringify({
                players: uIdsToLookUp
            }),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        }).then(response => response.json()).then(playerLookUp => {
            this.playerCache.set(uIds, playerLookUp);
            return playerLookUp;
        });
        for (const [key, value] of Object.entries(lookUpObject)) {
            responseObject[key] = value;
            this.playerCache.set(key, {
                uId: key,
                playerName: value
            });
        }

        return responseObject;
    },
}

export default PlayerLookUpService;
