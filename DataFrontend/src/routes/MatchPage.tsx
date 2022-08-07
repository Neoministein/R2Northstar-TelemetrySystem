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
    let scale = {} as GameMapScaleEntity;

    useEffect(() => {
        if (match) {
            new GameMapService().getMapScale(match.map).then(data => {scale = data});
        } else {
            navigate("/");
            return;
        }
        client = new MatchStateService().getMatchStateSocket(match.id);
        client.onmessage = (message) => {
            matchState = JSON.parse(message.data);
        }
    })

    let bg : p5.Image;
    let playerBlue : p5.Image;
    let playerOrange : p5.Image;

    let playerBlueDead : p5.Image;
    let playerOrangeDead : p5.Image;

    let playerBlueTitan : p5.Image;
    let playerOrangeTitan : p5.Image;

    let npcIconBlue : p5.Image;
    let npcIconOrange : p5.Image

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

            bg = p5Instance.loadImage('/img/map/' + match.map + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(1024, 1024, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(bg as p5.Image, 255 as number);
            if(matchState) {
                renderPlayers(p5Instance);
                renderNpcs(p5Instance);
            }
        };
    }

    function renderPlayers(p5Instance : P5Instance) : void {
        const imageScale : number = scale.scale / 8
        for(const player of matchState.players) {
            p5Instance.push()
            p5Instance.translate((player.position.x + scale.xOffset) / scale.scale, (player.position.y * -1 + scale.yOffset) / scale.scale);

            let playerIcon : p5.Image;

            if (player.isAlive) {
                p5Instance.rotate(Math.PI / 180 * (90 - player.rotation.y));
                if (player.isTitan) {
                    if(player.team === 2) {
                        playerIcon = playerBlueTitan;
                    } else {
                        playerIcon = playerOrangeTitan;
                    }
                    p5Instance.image(playerIcon, -(15 / imageScale), -(19 / imageScale),30 / imageScale,38 / imageScale);
                } else {
                    if(player.team === 2) {
                        playerIcon = playerBlue;
                    } else {
                        playerIcon = playerOrange;
                    }
                    p5Instance.image(playerIcon, -(9.5 / imageScale), -(12.5 / imageScale),19 / imageScale,25 / imageScale);
                }
            } else {
                if(player.team === 2) {
                    playerIcon = playerBlueDead;
                } else {
                    playerIcon = playerOrangeDead;
                }
                p5Instance.image(playerIcon, -(9.5 / imageScale), -(12.5 / imageScale),15 / imageScale,19 / imageScale);
            }
            p5Instance.pop()
        }
    }

    function renderNpcs(p5Instance : P5Instance) : void {
        const imageScale : number = scale.scale / 8
        for(const npc of matchState.npcs) {
            const npcIcon = getNpcIcon(npc.npcClass)
            p5Instance.push()
            p5Instance.translate((npc.position.x + scale.xOffset) / scale.scale, (npc.position.y * -1 + scale.yOffset) / scale.scale);
            p5Instance.image(npcIcon, -(30 / imageScale), -(30 / imageScale),30 / imageScale,30 / imageScale);
            p5Instance.pop();
        }
    }

    function getNpcIcon(npcClass: string, ) : p5.Image {
        /*
        switch (npcClass) {
            case "npc_soldier":
            case "npc_spectre":
            case "npc_stalker":
            case "npc_super_spectre": //Reaper
            case "npc_drone":
            case "npc_dropship":
            case "npc_turret_sentry":
            case "npc_turret_mega":
            case "npc_titan":
            default:
                break;
        }*/
        return npcIconOrange;
    }

    return (
        <div>
            <h2>{match.id}</h2>
            <ReactP5Wrapper sketch={sketch} />
        </div>
    );
}
