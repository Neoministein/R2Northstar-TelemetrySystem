import {GameMapEntity, HeatmapEntity} from "../service/GameMapService";
import p5 from "p5";
import {P5Instance, ReactP5Wrapper} from "react-p5-wrapper";

export interface HeatmapProps {
    heatmap: HeatmapEntity
    map: GameMapEntity
}

export default function Heatmap(props : HeatmapProps) {

    function sketch(p5Instance : P5Instance) {
        let bg: p5.Image

        p5Instance.preload = () => {
            bg = p5Instance.loadImage('/img/map/' + props.map.name + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(1024, 1024, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(bg as p5.Image, 255 as number);
            if(props.heatmap.data) {
                let highest = 25;
                for (let i = 0; i < props.heatmap.data.entries.length; i++) {
                    let c = p5Instance.color(props.heatmap.data.entries[i].count / highest * 255, 255, 255);

                    p5Instance.fill(c);
                    p5Instance.noStroke();
                    p5Instance.rect(props.heatmap.data.entries[i].x, props.heatmap.data.entries[i].y, 4, 4);
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