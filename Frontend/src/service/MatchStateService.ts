import {AppConfig} from "../AppConfig";

const W3CWebSocket = require('websocket').w3cwebsocket;

const MatchStateService = {

    getMatchStateSocket(id : string) : WebSocket {
        const wsUrl = AppConfig.wsUrl + "/state/output/" + id

        return new W3CWebSocket( wsUrl );
    }
}

export default MatchStateService
