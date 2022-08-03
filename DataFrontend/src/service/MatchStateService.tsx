import {AppConfig} from "../Config";

const W3CWebSocket = require('websocket').w3cwebsocket;

export class MatchStateService {

    getMatchStateSocket(id : string) : WebSocket {
        return new W3CWebSocket(AppConfig.wsUrl + "/state/output/" + id);
    }
}