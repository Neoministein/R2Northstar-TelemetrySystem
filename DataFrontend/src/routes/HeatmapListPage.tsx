import {useEffect, useState} from "react";
import {GameMapEntity, GameMapService} from "../service/GameMapService";
import {useNavigate} from "react-router-dom";
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";

export default function HeatmapListPage() {
    const navigate = useNavigate();
    const [maps, setMaps] = useState<GameMapEntity[]>([]);
    const gameMapService = new GameMapService();

    useEffect(() => {
        gameMapService.getAllMaps().then(data => setMaps(data));
    }, []); // eslint-disable-line react-hooks/exhaustive-deps
    return (
        <div>
            <div>
                <div className="card">
                    <DataTable value={maps} responsiveLayout="scroll" selectionMode="single" onSelectionChange={
                        e => {
                            navigate("/heatmap/map", {state: e.value});
                        }}>
                        <Column field="name" header="Map"></Column>
                        <Column field="type" header="Type"></Column>
                    </DataTable>
                </div>
            </div>
        </div>
);
}