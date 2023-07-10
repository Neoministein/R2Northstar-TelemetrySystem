import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {GameMapEntity, GameMapService, HeatmapEntity} from "../service/GameMapService";
import Heatmap from "../components/Heatmap";

export default function HeatmapMapPage() {
    const map = useLocation().state as GameMapEntity;
    const [heatmap, setHeatmap] = useState<HeatmapEntity>({} as HeatmapEntity);
    const navigate = useNavigate();

    useEffect(() => {
        if (map) {
            if (!heatmap?.data) {
                new GameMapService().getMapHeatmap(map.name).then(data => {
                    setHeatmap(data)
                });
            }
        } else {
            navigate("/heatmap");
            return;
        }
    }, [])

    return (
        <div>
            <Heatmap heatmap={heatmap} map={map}/>
        </div>
    );
}
