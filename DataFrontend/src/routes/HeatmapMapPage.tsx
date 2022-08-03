import {useLocation, useNavigate} from "react-router-dom";
import {MatchEntity} from "../service/MatchService";
import {useEffect} from "react";
import {MatchStateService} from "../service/MatchStateService";
import {P5Instance, ReactP5Wrapper,} from "react-p5-wrapper";
import {GameMapEntity, GameMapService, HeatmapEntity} from "../service/GameMapService";
import p5 from "p5";

export default function HeatmapMapPage() {
    const map = useLocation().state as GameMapEntity;
    const navigate = useNavigate();
    let heatmap = {} as HeatmapEntity;

    useEffect(() => {
        if (map) {
            new GameMapService().getMapHeatmap(map.name).then(data => {heatmap = data});
        } else {
            navigate("/heatmap");
            return;
        }
    })

    let bg : p5.Image;

    function sketch(p5Instance : P5Instance) {
        p5Instance.preload = () => {
            bg = p5Instance.loadImage('/img/map/' + map.name + '.png');
        }
        p5Instance.setup = () => p5Instance.createCanvas(1024, 1024, p5Instance.P2D);

        p5Instance.draw = () => {
            p5Instance.background(bg as p5.Image, 255 as number);
            //console.log(matchState)
            if(heatmap?.data) {
                let highest = 5;
                for (let i = 0; i < heatmap.data.entries.length; i++) {
                    let c = p5Instance.color(heatmap.data.entries[i].count / highest * 255, 255, 255);

                    p5Instance.fill(c);
                    p5Instance.noStroke();
                    //set(json.entries[i].posX, json.entries[i].posY, c);
                    p5Instance.rect(heatmap.data.entries[i].x, heatmap.data.entries[i].y, 4, 4);
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
