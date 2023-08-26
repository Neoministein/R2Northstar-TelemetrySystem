import {AppConfig} from "../AppConfig";
import ErrorUtils from "../utils/ErrorUtils";

export interface GameModeDistribution {
    mode: string
    count: number
    percent: number
}

const DashboardService = {
    getModeDistribution() : Promise<GameModeDistribution[]> {
        return fetch(AppConfig.apiUrl + "/mode/distribution" )
            .then(resp => { return ErrorUtils.parseResponse(resp);})
            .then(resp => { return resp.hits;});
    }
}

export default DashboardService
