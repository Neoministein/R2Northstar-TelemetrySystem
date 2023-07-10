import p5 from "p5";
import {AppConfig} from "../AppConfig";
import {P5CanvasInstance} from "@p5-wrapper/react";

export class ImageContainer {

    bg : p5.Image;
    playerBlue : p5.Image;
    playerOrange : p5.Image;

    playerBlueDead : p5.Image;
    playerOrangeDead : p5.Image;

    playerBlueTitan : p5.Image;
    playerOrangeTitan : p5.Image;

    npcIconBlue : p5.Image;
    npcIconOrange : p5.Image;
    npcIconGrey : p5.Image;

    npcSoliderBlue : p5.Image;
    npcSoliderOrange : p5.Image;
    npcSoliderGrey : p5.Image;

    npcSpectreBlue : p5.Image;
    npcSpectreOrange : p5.Image;
    npcSpectreGrey : p5.Image;

    npcStalkerBlue : p5.Image;
    npcStalkerOrange : p5.Image;
    npcStalkerGrey : p5.Image;

    npcReaperBlue : p5.Image;
    npcReaperOrange : p5.Image;
    npcReaperGrey : p5.Image;

    npcDroneBlue : p5.Image;
    npcDroneOrange: p5.Image;
    npcDroneGrey : p5.Image;

    npcDropshipBlue : p5.Image;
    npcDropshipOrange : p5.Image;
    npcDropshipGrey : p5.Image;

    npcTurretBlue : p5.Image;
    npcTurretOrange : p5.Image;
    npcTurretGrey : p5.Image;

    npcTitanBlue : p5.Image;
    npcTitanOrange : p5.Image;
    npcTitanGrey : p5.Image;

    constructor(p5Instance : P5CanvasInstance, map : string) {
        this.playerBlue = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-blue.png");
        this.playerOrange = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-orange.png"); // 19 25

        this.playerBlueDead = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-blue-dead.png"); // 41 53
        this.playerOrangeDead = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-orange-dead.png");

        this.playerBlueTitan = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-blue-titan.png"); // 30 38
        this.playerOrangeTitan = p5Instance.loadImage(AppConfig.minimapImgPath + "/icon/player-orange-titan.png");

        this.npcIconBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/npc-icon-blue.png');
        this.npcIconOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/npc-icon-orange.png');
        this.npcIconGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/npc-icon-grey.png');

        this.npcSoliderBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/grunt-blue.png');
        this.npcSoliderOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/grunt-orange.png');
        this.npcSoliderGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/grunt-grey.png');

        this.npcSpectreBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/spectre-blue.png');
        this.npcSpectreOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/spectre-orange.png');
        this.npcSpectreGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/spectre-grey.png');

        this.npcStalkerBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/stalker-blue.png');
        this.npcStalkerOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/stalker-orange.png');
        this.npcStalkerGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/stalker-grey.png');

        this.npcReaperBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/reaper-blue.png');
        this.npcReaperOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/reaper-orange.png');
        this.npcReaperGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/reaper-grey.png');

        this.npcDroneBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/drone-blue.png');
        this.npcDroneOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/drone-orange.png');
        this.npcDroneGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/drone-grey.png');

        this.npcDropshipBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/dropship-blue.png');
        this.npcDropshipOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/dropship-orange.png');
        this.npcDropshipGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/dropship-grey.png');


        this.npcTurretBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/turret-blue.png');
        this.npcTurretOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/turret-orange.png');
        this.npcTurretGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/turret-grey.png');

        this.npcTitanBlue = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/player-blue-titan-npc.png');
        this.npcTitanOrange = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/player-orange-npc-titan.png');
        this.npcTitanGrey = p5Instance.loadImage(AppConfig.minimapImgPath + '/icon/npc-titan-grey.png');

        this.bg = p5Instance.loadImage(AppConfig.minimapImgPath + '/map/' + map + '.png');
    }

    getNpcIcon(npcClass: string, team : number) : p5.Image {
        if (team === 3) {
            switch (npcClass) {
                case "npc_soldier":
                    return this.npcSoliderOrange;
                case "npc_spectre":
                    return this.npcSpectreOrange;
                case "npc_stalker":
                    return this.npcStalkerOrange;
                case "npc_super_spectre": //Reaper
                    return this.npcReaperOrange;
                case "npc_drone":
                    return this.npcDroneOrange;
                case "npc_dropship":
                    return this.npcDropshipOrange;
                case "npc_turret_sentry":
                case "npc_turret_mega":
                    return this.npcTurretOrange;
                case "npc_titan":
                    return this.npcTitanOrange;
                default:
                    return this.npcIconOrange;
            }
        } else if (team === 2) {
            switch (npcClass) {
                case "npc_soldier":
                    return this.npcSoliderBlue;
                case "npc_spectre":
                    return this.npcSpectreBlue;
                case "npc_stalker":
                    return this.npcStalkerBlue;
                case "npc_super_spectre": //Reaper
                    return this.npcReaperBlue;
                case "npc_drone":
                    return this.npcDroneBlue;
                case "npc_dropship":
                    return this.npcDropshipBlue;
                case "npc_turret_sentry":
                case "npc_turret_mega":
                    return this.npcTurretBlue;
                case "npc_titan":
                    return this.npcTitanBlue;
                default:
                    return this.npcIconBlue;
            }
        }
        switch (npcClass) {
            case "npc_soldier":
                return this.npcSoliderGrey;
            case "npc_spectre":
                return this.npcSpectreGrey;
            case "npc_stalker":
                return this.npcStalkerGrey;
            case "npc_super_spectre": //Reaper
                return this.npcReaperGrey;
            case "npc_drone":
                return this.npcDroneGrey;
            case "npc_dropship":
                return this.npcDropshipGrey;
            case "npc_turret_sentry":
            case "npc_turret_mega":
                return this.npcTurretGrey;
            case "npc_titan":
                return this.npcTitanGrey;
            default:
                return this.npcIconGrey;
        }
    }
}
