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

    const toDateString = (rowData : MatchEntity) => {
        return new Date(rowData.startDate).toISOString().replace("T", " ").split(".")[0];
    }

    const numberOfPlayers = (rowData: MatchEntity)  => {
        return rowData.numberOfPlayers + "/" + "?";
    }

    return (
        <div>
            <div className="card">
                <DataTable value={matches} responsiveLayout="scroll" selectionMode="single" onSelectionChange={
                    e => {
                        navigate("match/", {state: e.value});
                    }}>
                    <Column header="Ns Server Name" field="nsServerName"></Column>
                    <Column header="Players" body={numberOfPlayers}></Column>
                    <Column header="Map" field="map"></Column>
                    <Column header="Gamemode" field="gamemode"></Column>
                    <Column header="Start date" body={toDateString}></Column>
                </DataTable>
            </div>
        </div>
    );
}