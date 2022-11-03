import {GameMapEntity, HeatmapEntity} from "../service/GameMapService";
import p5 from "p5";
import {P5Instance, ReactP5Wrapper} from "react-p5-wrapper";

export interface HeatmapProps {
    heatmap: HeatmapEntity
    map: GameMapEntity
}

export default function Heatmap({heatmap, map} : HeatmapProps) {

    let canvasSize = getCanvasSize();

    function getCanvasSize() : number {
        return window.screen.height - 125;
    }

    function scalePosition(position : number) : number {
        return position / 1024 * canvasSize;
    }

    function sketch(p5Instance : P5Instance) {
        let bg: p5.Image

        p5Instance.preload = () => {
            bg = p5Instance.loadImage('/img/map/' + map.name + '.png');
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

    return (
        <div>
            <ReactP5Wrapper sketch={sketch} />
        </div>
    );
}