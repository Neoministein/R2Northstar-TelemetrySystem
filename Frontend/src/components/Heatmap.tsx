import p5 from "p5";
import dynamic from "next/dynamic";
import {P5CanvasInstance} from "@p5-wrapper/react";
import {GameMap, HeatmapEntity} from "../service/MapService";
import {AppConfig} from "../AppConfig";

export interface HeatmapProps {
    heatmap: HeatmapEntity
    map: GameMap
}

export default function Heatmap({heatmap, map} : HeatmapProps) {

    const ReactP5Wrapper = dynamic<typeof import("@p5-wrapper/react").ReactP5Wrapper>(
        // @ts-ignore
        () => import("@p5-wrapper/react").then(m => {
            return m.ReactP5Wrapper;
        }),
        { ssr: false }
    );

    let lazyCanvasHolder = null;

    function getCanvasSize() : number {
        if (lazyCanvasHolder === null) {
            lazyCanvasHolder = window.screen.height - 200
        }
        return lazyCanvasHolder;
    }

    function scalePosition(position : number) : number {
        return position / 1024 * getCanvasSize();
    }

    function sketch(p5Instance : P5CanvasInstance) {
        let bg: p5.Image

        p5Instance.preload = () => {
            bg = p5Instance.loadImage(AppConfig.minimapImgPath + "/map/" + map.name + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(getCanvasSize(), getCanvasSize(), p5Instance.P2D);

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

    return (
        <div>
            <ReactP5Wrapper sketch={sketch} />
        </div>
    );
}
