import {AppConfig} from "../Config";

export class BackendService {


    getPlayerKills(map : string) : Promise<?> {
        return fetch(AppConfig.apiUrl + "/map/" + map ).then(response => response.json());
    }
}