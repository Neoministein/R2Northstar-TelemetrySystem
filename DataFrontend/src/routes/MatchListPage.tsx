import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";
import {useEffect, useState} from "react";
import {MatchEntity, MatchService} from "../service/MatchService";
import {useNavigate} from "react-router-dom";


export default function MatchListPage() {
    const navigate = useNavigate();
    const [matches, setMatches] = useState<MatchEntity[]>([]);
    const matchService = new MatchService();

    useEffect(() => {
        matchService.getRunningMatches()
            .then(data => setMatches(data));
    }, []); // eslint-disable-line react-hooks/exhaustive-deps
    return (
        <div>
            <div className="card">
                <DataTable value={matches} responsiveLayout="scroll" selectionMode="single" onSelectionChange={
                    e => {
                        navigate("match/", {state: e.value});
                    }}>
                    <Column field="nsServerName" header="Ns Server Name"></Column>
                    <Column field="map" header="Map"></Column>
                    <Column field="gamemode" header="Gamemode"></Column>
                    <Column field="startDate" header="Start date"></Column>
                </DataTable>
            </div>
        </div>
    );
}