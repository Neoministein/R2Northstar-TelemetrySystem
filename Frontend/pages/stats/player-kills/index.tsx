import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTable from '../../../src/components/TopResultTable';

const PlayerKillPage = () => {

    return (
        <TopResultTable
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopPlayerKills(tags)}
            columns={
                [<Column field="playerName" header="PlayerName"/>,
                <Column field="PGS_PILOT_KILLS" header="Player Kills"/>]}/>
    );
};

export default PlayerKillPage;
