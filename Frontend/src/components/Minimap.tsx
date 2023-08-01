import * as React from "react";
import {MutableRefObject, useEffect, useState} from "react";
import {P5CanvasInstance} from "@p5-wrapper/react";
import ClientSideReactP5 from "./ClientSideReactP5";
import {useRouter} from "next/router";
import {MatchEntity} from "../service/MatchService";
import {MatchStateWrapper} from "../utils/MatchStateWrapper";
import {ImageContainer} from "../utils/ImageContainer";
import MatchStateService from "../service/MatchStateService";

export interface MinimapProps {
    match: MatchEntity
    parentObject: MutableRefObject<any>
}

export default function Minimap({match, parentObject} : MinimapProps) {

    const router = useRouter();
    const [matchStateWrapper] = useState<MatchStateWrapper>(new MatchStateWrapper())
    const [canvasSize, setCanvasSize] = useState(
        {
            width: parentObject.current.clientWidth,
            height: parentObject.current.clientHeight
        }
    );
    let wsClient = null;
    let imageContainer : ImageContainer;

    useEffect(() => {
        if (match === undefined) {
            return;
        }
        wsClient = MatchStateService.getMatchStateSocket(match.id)
        wsClient.onmessage = (message) => {
            if (message.data === "MATCH_END") {
                router.push('/finished/match/' + match.id)
            } else {
                matchStateWrapper.setState(JSON.parse(message.data));
            }
        }

    },[match])

    useEffect(() => {
        const handleWindowResize = () => {
            if (parentObject != undefined) {
                // @ts-ignore
                setCanvasSize({
                    width: parentObject.current.clientWidth,
                    height: parentObject.current.clientHeight
                });
            }

        };

        window.addEventListener('resize', handleWindowResize);

        return () => {
            window.removeEventListener('resize', handleWindowResize);
        };
    });

    function getCanvasSize() : number {
        if (canvasSize.width < canvasSize.height) {
            return canvasSize.width - 15;
        } else {
            return canvasSize.height - 100;
        }
    }

    function sketch(p5Instance : P5CanvasInstance) {
        p5Instance.preload = () => {
            imageContainer = new ImageContainer(p5Instance, match.map);
        }
        p5Instance.setup = () => p5Instance.createCanvas(getCanvasSize(), getCanvasSize(), p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(imageContainer.bg, 255 as number);
            if(matchStateWrapper.getState()) {
                renderPlayers(p5Instance);
                renderNpcs(p5Instance);
            }
        };
    }

    function scalePosition(position : number) : number {
        return position / 1024 * getCanvasSize();
    }

    function renderPlayers(p5Instance : P5CanvasInstance) : void {
        const imageScale : number = match.mapDetails.scale.scale / 8
        for(const player of matchStateWrapper.getState().players) {
            p5Instance.push()
            p5Instance.translate(
                scalePosition((player.position.x + match.mapDetails.scale.xOffset) / match.mapDetails.scale.scale),
                scalePosition((player.position.y * -1 + match.mapDetails.scale.yOffset) / match.mapDetails.scale.scale));

            // @ts-ignore
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

    function renderNpcs(p5Instance : P5CanvasInstance) : void {
        const imageScale : number = match.mapDetails.scale.scale / 8
        for(const npc of matchStateWrapper.getState().npcs) {
            const npcIcon = imageContainer.getNpcIcon(npc.entityType, npc.team)
            let scale = [30, 30, 30, 30]
            if (npc.entityType === "npc_titan") {
                scale = [22.5 , 28.5 , 34.5, 43.5]
            } else if (npc.entityType === "npc_dropship"){
                scale = [36 , 33 , 36, 33]
            }

            p5Instance.push()
            p5Instance.translate(
                scalePosition((npc.position.x + match.mapDetails.scale.xOffset) / match.mapDetails.scale.scale),
                scalePosition((npc.position.y * -1 + match.mapDetails.scale.yOffset) / match.mapDetails.scale.scale));

            if (npc.entityType === "npc_titan" || npc.entityType === "npc_dropship") {
                p5Instance.rotate(Math.PI / 180 * (90 - npc.rotation.y));
            }


            p5Instance.image(npcIcon,
                scalePosition(-(scale[0] / imageScale)),
                scalePosition(-(scale[1] / imageScale)),
                scalePosition(scale[2] / imageScale),
                scalePosition(scale[3] / imageScale));
            p5Instance.pop();
        }
    }


    return (match != null? (<ClientSideReactP5 sketchFunc={sketch} shouldComponentUpdate={false} />) : null);
}
