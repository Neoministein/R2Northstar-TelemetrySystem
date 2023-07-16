import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTableV2 from "../../../src/components/TopResultTableV2";

const PlayerKillPage = () => {

    return (
        <TopResultTableV2
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopPlayerKills(tags)}
            columns={
                [<Column field="playerName" header="PlayerName"/>,
                <Column field="PGS_PILOT_KILLS" header="Player Kills"/>]}/>
    );
};

export default PlayerKillPage;
