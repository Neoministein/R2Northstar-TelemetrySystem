import {useLocation, useNavigate} from "react-router-dom";
import {MatchEntity} from "../service/MatchService";
import {useEffect} from "react";
import {MatchStateService} from "../service/MatchStateService";
import {P5Instance, ReactP5Wrapper,} from "react-p5-wrapper";
import {GameMapScaleEntity, GameMapService} from "../service/GameMapService";
import p5 from "p5";

export default function MatchPage() {
    const match = useLocation().state as MatchEntity;
    const navigate = useNavigate();
    let client;
    let matchState : any;
    let mapScale = {} as GameMapScaleEntity;
    let canvasSize = getCanvasSize();

    useEffect(() => {
        if (match) {
            new GameMapService().getMapScale(match.map).then(data => {mapScale = data});
        } else {
            navigate("/");
            return;
        }
        client = new MatchStateService().getMatchStateSocket(match.id);
        client.onmessage = (message) => {
            matchState = JSON.parse(message.data);
        }
    });

    function getCanvasSize() : number {
        return window.screen.height - 125;
    }

    let bg : p5.Image;
    let playerBlue : p5.Image;
    let playerOrange : p5.Image;

    let playerBlueDead : p5.Image;
    let playerOrangeDead : p5.Image;

    let playerBlueTitan : p5.Image;
    let playerOrangeTitan : p5.Image;

    let npcIconBlue : p5.Image;
    let npcIconOrange : p5.Image;
    let npcIconGrey : p5.Image;

    let npcSoliderBlue : p5.Image;
    let npcSoliderOrange : p5.Image;
    let npcSoliderGrey : p5.Image;

    let npcSpectreBlue : p5.Image;
    let npcSpectreOrange : p5.Image;
    let npcSpectreGrey : p5.Image;

    let npcStalkerBlue : p5.Image;
    let npcStalkerOrange : p5.Image;
    let npcStalkerGrey : p5.Image;

    let npcReaperBlue : p5.Image;
    let npcReaperOrange : p5.Image;
    let npcReaperGrey : p5.Image;

    let npcDroneBlue : p5.Image;
    let npcDroneOrange: p5.Image;
    let npcDroneGrey : p5.Image;

    let npcDropshipBlue : p5.Image;
    let npcDropshipOrange : p5.Image;
    let npcDropshipGrey : p5.Image;

    let npcTurretBlue : p5.Image;
    let npcTurretOrange : p5.Image;
    let npcTurretGrey : p5.Image;

    let npcTitanBlue : p5.Image;
    let npcTitanOrange : p5.Image;
    let npcTitanGrey : p5.Image;

    function sketch(p5Instance : P5Instance) {
        p5Instance.preload = () => {
            playerBlue = p5Instance.loadImage("/img/icon/player-blue.png");
            playerOrange = p5Instance.loadImage("/img/icon/player-orange.png"); // 19 25

            playerBlueDead = p5Instance.loadImage("/img/icon/player-blue-dead.png"); // 41 53
            playerOrangeDead = p5Instance.loadImage("/img/icon/player-orange-dead.png");

            playerBlueTitan = p5Instance.loadImage("/img/icon/player-blue-titan.png"); // 30 38
            playerOrangeTitan = p5Instance.loadImage("/img/icon/player-orange-titan.png");

            npcIconBlue = p5Instance.loadImage('/img/icon/npc-icon-blue.png');
            npcIconOrange = p5Instance.loadImage('/img/icon/npc-icon-orange.png');
            npcIconGrey = p5Instance.loadImage('/img/icon/npc-icon-grey.png');

            npcSoliderBlue = p5Instance.loadImage('/img/icon/grunt-blue.png');
            npcSoliderOrange = p5Instance.loadImage('/img/icon/grunt-orange.png');
            npcSoliderGrey = p5Instance.loadImage('/img/icon/grunt-grey.png');

            npcSpectreBlue = p5Instance.loadImage('/img/icon/spectre-blue.png');
            npcSpectreOrange = p5Instance.loadImage('/img/icon/spectre-orange.png');
            npcSpectreGrey = p5Instance.loadImage('/img/icon/spectre-grey.png');

            npcStalkerBlue = p5Instance.loadImage('/img/icon/stalker-blue.png');
            npcStalkerOrange = p5Instance.loadImage('/img/icon/stalker-orange.png');
            npcStalkerGrey = p5Instance.loadImage('/img/icon/stalker-grey.png');

            npcReaperBlue = p5Instance.loadImage('/img/icon/reaper-blue.png');
            npcReaperOrange = p5Instance.loadImage('/img/icon/reaper-orange.png');
            npcReaperGrey = p5Instance.loadImage('/img/icon/reaper-grey.png');

            npcDroneBlue = p5Instance.loadImage('/img/icon/drone-blue.png');
            npcDroneOrange = p5Instance.loadImage('/img/icon/drone-orange.png');
            npcDroneGrey = p5Instance.loadImage('/img/icon/drone-grey.png');

            npcDropshipBlue = p5Instance.loadImage('/img/icon/dropship-blue.png');
            npcDropshipOrange = p5Instance.loadImage('/img/icon/dropship-orange.png');
            npcDropshipGrey = p5Instance.loadImage('/img/icon/dropship-grey.png');

            npcTitanBlue = p5Instance.loadImage('/img/icon/player-blue-titan-npc.png');
            npcTitanOrange = p5Instance.loadImage('/img/icon/player-orange-npc-titan.png');
            npcTitanGrey = p5Instance.loadImage('/img/icon/npc-titan-grey.png');

            bg = p5Instance.loadImage('/img/map/' + match.map + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(canvasSize, canvasSize, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(bg as p5.Image, 255 as number);
            if(matchState) {
                renderPlayers(p5Instance);
                renderNpcs(p5Instance);
            }
        };
    }

    function scalePosition(position : number) : number {
        return position / 1024 * canvasSize;
    }

    function renderPlayers(p5Instance : P5Instance) : void {
        const imageScale : number = mapScale.scale / 8
        for(const player of matchState.players) {
            p5Instance.push()
            p5Instance.translate(
                scalePosition((player.position.x + mapScale.xOffset) / mapScale.scale),
                scalePosition((player.position.y * -1 + mapScale.yOffset) / mapScale.scale));

            let playerIcon : p5.Image;

            if (player.isAlive) {
                p5Instance.rotate(Math.PI / 180 * (90 - player.rotation.y));
                if (player.isTitan) {
                    if(player.team === 2) {
                        playerIcon = playerBlueTitan;
                    } else {
                        playerIcon = playerOrangeTitan;
                    }
                    p5Instance.image(playerIcon,
                        scalePosition(-(15 / imageScale)),
                        scalePosition(-(19 / imageScale)),
                        scalePosition(30 / imageScale),
                        scalePosition(38 / imageScale));
                } else {
                    if(player.team === 2) {
                        playerIcon = playerBlue;
                    } else {
                        playerIcon = playerOrange;
                    }
                    p5Instance.image(playerIcon,
                        scalePosition(-(9.5 / imageScale)),
                        scalePosition(-(12.5 / imageScale)),
                        scalePosition(19 / imageScale),
                        scalePosition(25 / imageScale));
                }
            } else {
                if(player.team === 2) {
                    playerIcon = playerBlueDead;
                } else {
                    playerIcon = playerOrangeDead;
                }
                p5Instance.image(playerIcon,
                    scalePosition(-(9.5 / imageScale)),
                    scalePosition(-(12.5 / imageScale)),
                    scalePosition(15 / imageScale),
                    scalePosition(19 / imageScale));
            }
            p5Instance.pop()
        }
    }

    function renderNpcs(p5Instance : P5Instance) : void {
        const imageScale : number = mapScale.scale / 8
        for(const npc of matchState.npcs) {
            const npcIcon = getNpcIcon(npc.npcClass, npc.team)
            p5Instance.push()
            p5Instance.translate(
                scalePosition((npc.position.x + mapScale.xOffset) / mapScale.scale),
                scalePosition((npc.position.y * -1 + mapScale.yOffset) / mapScale.scale));

            if (npc.npcClass === "npc_titan" || npc.npcClass === "npc_dropship") {
                p5Instance.rotate(Math.PI / 180 * (90 - npc.rotation.y));
            }
            p5Instance.image(npcIcon,
                scalePosition(-(30 / imageScale)),
                scalePosition(-(30 / imageScale)),
                scalePosition(30 / imageScale),
                scalePosition(30 / imageScale));
            p5Instance.pop();
        }
    }

    function getNpcIcon(npcClass: string, team : number) : p5.Image {
        if (team === 3) {
            switch (npcClass) {
                case "npc_soldier":
                    return npcSoliderOrange;
                case "npc_spectre":
                    return npcSpectreOrange;
                case "npc_stalker":
                    return npcStalkerOrange;
                case "npc_super_spectre": //Reaper
                    return npcReaperOrange;
                case "npc_drone":
                    return npcDroneOrange;
                case "npc_dropship":
                    return npcDropshipOrange;
                case "npc_turret_sentry":
                case "npc_turret_mega":
                    return npcTurretOrange;
                case "npc_titan":
                    return npcTitanOrange;
                default:
                    return npcIconOrange;
            }
        } else if (team === 2) {
            switch (npcClass) {
                case "npc_soldier":
                    return npcSoliderBlue;
                case "npc_spectre":
                    return npcSpectreBlue;
                case "npc_stalker":
                    return npcStalkerBlue;
                case "npc_super_spectre": //Reaper
                    return npcReaperBlue;
                case "npc_drone":
                    return npcDroneBlue;
                case "npc_dropship":
                    return npcDropshipBlue;
                case "npc_turret_sentry":
                case "npc_turret_mega":
                    return npcTurretBlue;
                case "npc_titan":
                    return npcTitanBlue;
                default:
                    return npcIconBlue;
            }
        }
        switch (npcClass) {
            case "npc_soldier":
                return npcSoliderGrey;
            case "npc_spectre":
                return npcSpectreGrey;
            case "npc_stalker":
                return npcStalkerGrey;
            case "npc_super_spectre": //Reaper
                return npcReaperGrey;
            case "npc_drone":
                return npcDroneGrey;
            case "npc_dropship":
                return npcDropshipGrey;
            case "npc_turret_sentry":
            case "npc_turret_mega":
                return npcTurretGrey;
            case "npc_titan":
                return npcTitanGrey;
            default:
                return npcIconGrey;
        }
    }

    return (
        <div>
            <h2>{match.nsServerName}</h2>
            <div>
                <ReactP5Wrapper sketch={sketch} />
            </div>
        </div>
    );
}
