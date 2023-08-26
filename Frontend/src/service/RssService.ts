import {AppConfig} from "../AppConfig";

const W3CWebSocket = require('websocket').w3cwebsocket;

export interface RssResponse {
    version: string
    title: string
    description: string
    items: RssItem[]
}

export interface RssItem {
    id: string
    title: string
    contentText: string
    datePublished: number
    icon: string
}

const RssService = {

    getRssFeed(feedName: string) : Promise<RssResponse> {
        return fetch(AppConfig.apiUrl + "/rss/" + feedName).then(response => response.json());
    },

    getRssFeedSocket(feedName : string) : WebSocket {
        const wsUrl = AppConfig.wsUrl + "/rss/" + feedName

        return new W3CWebSocket( wsUrl );
    }
}

export default RssService;
