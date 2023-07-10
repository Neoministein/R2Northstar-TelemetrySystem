import {AppConfig} from "../AppConfig";
import BackendErrorUtils from "../utils/BackendErrorUtils";

export interface GameMap {
    name: string
    displayName: string
    type: string
    scale: MapScale
}

export interface MapScale {
    xOffset: number
    yOffset: number
    scale: number
}

type CachedGameMap = GameMap | Promise<GameMap>;

const MapService = {

    cachedMapDetails: new Map<string, CachedGameMap>(),

    async getMapDetails(mapId : string) : Promise<GameMap> {
        const cachedPlayer = this.cachedMapDetails.get(mapId);
        if(cachedPlayer === undefined) {
            const promise = fetch(AppConfig.apiUrl + "/map/" + mapId )
                .then(resp => { return BackendErrorUtils.parseResponse(resp);})
                .then(playerLookUp => {
                    this.cachedMapDetails.set(mapId, playerLookUp);
                    return playerLookUp;
                });

            this.cachedMapDetails.set(mapId, promise);

            return promise;
        } else if (cachedPlayer instanceof Promise) {
            return cachedPlayer;
        }

        return Promise.resolve(cachedPlayer);
    }
}

export default MapService;
