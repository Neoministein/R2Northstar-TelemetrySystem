import {useLocation, useNavigate} from "react-router-dom";
import {MatchEntity} from "../service/MatchService";
import {useEffect, useState} from "react";
import {MatchStateService} from "../service/MatchStateService";
import {P5Instance, ReactP5Wrapper,} from "react-p5-wrapper";
import {GameMapScaleEntity, GameMapService} from "../service/GameMapService";
import p5 from "p5";
import {ImageContainer} from "../util/ImageContainer";

export default function MatchPlayingPage() {
    const [time, setTime] = useState<string>("00:00");
    const match = useLocation().state as MatchEntity;
    const navigate = useNavigate();
    let client;
    let matchState : any;
    let mapScale = {} as GameMapScaleEntity;
    let canvasSize = getCanvasSize();
    let imageContainer : ImageContainer;

    useEffect(() => {
        if (match) {
            new GameMapService().getMapScale(match.map).then(data => {mapScale = data});
        } else {
            navigate("/");
            return;
        }
        client = new MatchStateService().getMatchStateSocket(match.id);
        client.onmessage = (message) => {
            if (message.data === "MATCH_END") {
                navigate("match/", {state: match})
            } else {
                matchState = JSON.parse(message.data);
                const timePassedFormatted = millisToMinutesAndSeconds(message.data.timePassed);
                if (timePassedFormatted != time) {
                    setTime(timePassedFormatted);
                }
            }

        }
    });

    function millisToMinutesAndSeconds(millis : number) : string {
        const minutes = Math.floor(millis / 60000);
        const seconds : number = parseInt(((millis % 60000) / 1000).toFixed(0));
        return minutes + ":" + (seconds < 10 ? '0' : '') + seconds;
    }

    function getCanvasSize() : number {
        return window.screen.height - 125;
    }

    function sketch(p5Instance : P5Instance) {
        p5Instance.preload = () => {
            imageContainer = new ImageContainer(p5Instance, match.map);
        }
        p5Instance.setup = () => p5Instance.createCanvas(canvasSize, canvasSize, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(imageContainer.bg, 255 as number);
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
                        playerIcon = imageContainer.playerBlueTitan;
                    } else {
                        playerIcon = imageContainer.playerOrangeTitan;
                    }
                    p5Instance.image(playerIcon,
                        scalePosition(-(15 / imageScale)),
                        scalePosition(-(19 / imageScale)),
                        scalePosition(30 / imageScale),
                        scalePosition(38 / imageScale));
                } else {
                    if(player.team === 2) {
                        playerIcon = imageContainer.playerBlue;
                    } else {
                        playerIcon = imageContainer.playerOrange;
                    }
                    p5Instance.image(playerIcon,
                        scalePosition(-(9.5 / imageScale)),
                        scalePosition(-(12.5 / imageScale)),
                        scalePosition(19 / imageScale),
                        scalePosition(25 / imageScale));
                }
            } else {
                if(player.team === 2) {
                    playerIcon = imageContainer.playerBlueDead;
                } else {
                    playerIcon = imageContainer.playerOrangeDead;
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
            const npcIcon = imageContainer.getNpcIcon(npc.npcClass, npc.team)
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


    return (
        <div>
            <h2>{match.nsServerName} {time}</h2>
            <div>
                <ReactP5Wrapper sketch={sketch} />
            </div>
        </div>
    );
}
