import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTable from '../../../src/components/TopResultTable';

const NpcKillPage = () => {

    return (
        <TopResultTable
            title="Top Npc Kills"
            getGlobalRanking={(tags) => StatsService.getTopNpcKills(tags)}
            columns={
                [<Column field="playerName" header="PlayerName"/>,
                <Column field="PGS_NPC_KILLS" header="Npc Kills"/>]}/>
    );
};

export default NpcKillPage;
