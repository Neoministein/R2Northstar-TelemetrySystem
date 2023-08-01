import {useEffect, useState} from "react";
import MatchService, {MatchEntity} from "../../../src/service/MatchService";
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";
import {useRouter} from "next/router";
import I18nService from "../../../src/service/I18nService";


const MatchListPage = () => {
    const [matches, setMatches] = useState<MatchEntity[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const router = useRouter();

    useEffect(() => {
        MatchService.getRunningMatches()
            .then(data => setMatches(data))
            .then(() => setLoading(false));
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const toDateString = (rowData : MatchEntity) => {
        return new Date(rowData.startDate).toISOString().replace("T", " ").split(".")[0];
    }

    const numberOfPlayers = (rowData: MatchEntity) => {
        return rowData.numberOfPlayers + "/" + rowData.maxPlayers;
    }


    const translateMap = (rowData : MatchEntity) => {
        return I18nService.translate(rowData.map);
    }

    const handleClick = (value : any) => {
        router.push('/live/match/[id]', '/live/match/' + value.id );
    };

    return (
        <div className="grid">
            <div className="col-12">
                <div className="card">
                    <DataTable value={matches} loading={loading} scrollable={true} selectionMode="single" onSelectionChange={e => {handleClick(e.value);}}>
                        <Column header="Ns Server Name" sortable field="nsServerName"/>
                        <Column header="Players"        sortable field="numberOfPlayers" body={numberOfPlayers}/>
                        <Column header="Map"            sortable field="map" body={translateMap}/>
                        <Column header="Gamemode"       sortable field="gamemode"/>
                        <Column header="Start date"     sortable field="startDate" body={toDateString}/>
                    </DataTable>
                </div>
            </div>
        </div>
    );
}

export default MatchListPage;
