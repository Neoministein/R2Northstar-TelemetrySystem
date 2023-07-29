import p5 from "p5";
import {P5CanvasInstance} from "@p5-wrapper/react";
import {GameMap, HeatmapEntity} from "../service/MapService";
import {AppConfig} from "../AppConfig";
import {MutableRefObject, useEffect, useState} from "react";
import ClientSideReactP5 from "./ClientSideReactP5";

export interface HeatmapProps {
    heatmap: HeatmapEntity
    map: GameMap
    parentObject: MutableRefObject<any>
}

export default function Heatmap({heatmap, map, parentObject} : HeatmapProps) {

    const [canvasSize, setCanvasSize] = useState(parentObject.current.clientWidth);

    useEffect(() => {
        const handleWindowResize = () => {
            setCanvasSize(parentObject.current.clientWidth);
        };

        window.addEventListener('resize', handleWindowResize);

        return () => {
            window.removeEventListener('resize', handleWindowResize);
        };
    });

    function scalePosition(position : number) : number {
        return position / 1024 * canvasSize;
    }

    function sketch(p5Instance : P5CanvasInstance) {
        let bg: p5.Image

        p5Instance.preload = () => {
            bg = p5Instance.loadImage(AppConfig.minimapImgPath + "/map/" + map.name + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(canvasSize, canvasSize, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(bg, 255 as number);
            if(heatmap.data) {
                let highest = 25;
                for (const element of heatmap.data.entries) {
                    let c = p5Instance.color(element.count / highest * 255, 255, 255);

                    p5Instance.fill(c);
                    p5Instance.noStroke();
                    p5Instance.rect(
                        scalePosition(element.x),
                        scalePosition(element.y),
                        scalePosition(4),
                        scalePosition(4));
                }
            }
        };
    }

    return <ClientSideReactP5 sketchFunc={sketch}/>
}
