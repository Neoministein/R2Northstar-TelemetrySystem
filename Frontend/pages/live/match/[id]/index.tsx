import {useRouter} from 'next/router';
import * as React from "react";
import {useEffect, useState} from "react";
import MatchService, {MatchEntity} from "../../../../src/service/MatchService";
import MatchStateService from "../../../../src/service/MatchStateService";
import {ImageContainer} from "../../../../src/utils/ImageContainer";
import {P5CanvasInstance, ReactP5Wrapper} from "@p5-wrapper/react";
import dynamic from "next/dynamic";
import useQuery from "../../../../src/utils/useQuery";
import {MatchStateWrapper} from "../../../../src/utils/MatchStateWrapper";

const MatchPage = () => {

    const ReactP5Wrapper = dynamic<typeof import("@p5-wrapper/react").ReactP5Wrapper>(
        // @ts-ignore
        () => import("@p5-wrapper/react").then(m => {
            return m.ReactP5Wrapper;
        }),
        { ssr: false }
    );

    const router = useRouter();
    const query = useQuery();
    const [match, setMatch] = useState<MatchEntity>();
    const [matchStateWrapper, setMatchStateWrapper] = useState<MatchStateWrapper>(new MatchStateWrapper())
    let wsClient = null;
    //let currentMatchState = null;
    let lazyCanvastHolder = null;
    let imageContainer : ImageContainer;




    useEffect(() => {
        if (!query) {
            return;
        }
        MatchService.getMatch(query.id as string)
            .then( result => setMatch(result))
            .catch( ex => {
                // @ts-ignore
                window.PrimeToast.show({ severity: 'error', summary: 'Match not found', detail: `The match that you tried to navigate to does not exist:`, life: 3000 });
                router.push('/live/match')
            })
        wsClient = MatchStateService.getMatchStateSocket(query.id as string)
        wsClient.onmessage = (message) => {
            if (message.data === "MATCH_END") {
                router.push('/live/match')
            } else {
                matchStateWrapper.setState(JSON.parse(message.data));
                //const timePassedFormatted = millisToMinutesAndSeconds(matchState.timePassed);
                //if (timePassedFormatted != time) {
                //    //Currently disabled because it causes p5 to reload every time
                //    //setTime(timePassedFormatted);
                //}
            }
        }

    },[query])

    function getCanvasSize() : number {
        if (lazyCanvastHolder === null) {
            lazyCanvastHolder = window.screen.height - 200
        }
        return lazyCanvastHolder;
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


    return (
        <div>
            <h2>{match?.nsServerName}</h2>
            {
                match != null? (
                        // @ts-ignore
                        <ReactP5Wrapper sketch={sketch} shouldComponentUpdate={false} />
                    ) :
                    <div/>
            }
        </div>
    );
};

export default MatchPage;

