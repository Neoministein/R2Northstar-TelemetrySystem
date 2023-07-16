import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTableV2 from "../../../src/components/TopResultTableV2";

const NpcKillPage = () => {

    return (
        <TopResultTableV2
            title="Top Npc Kills"
            getGlobalRanking={(tags) => StatsService.getTopNpcKills(tags)}
            columns={
                [<Column field="playerName" header="PlayerName"/>,
                <Column field="PGS_NPC_KILLS" header="Npc Kills"/>]}/>
    );
};

export default NpcKillPage;
