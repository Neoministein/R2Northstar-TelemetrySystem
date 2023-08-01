import {Column} from 'primereact/column';
import StatsService from '../../../../../src/service/StatsService';
import TopResultTableV2 from "../../../../../src/components/TopResultTableV2";
import I18nService from "../../../../../src/service/I18nService";

const NpcKillPage = () => {

    return (
        <TopResultTableV2
            title="Top Npc Kills"
            getGlobalRanking={(tags) => StatsService.getTopNpcKills(tags)}
            columns={
                [
                    <Column header={I18nService.translate("playerName")}    field="playerName" />,
                    <Column header={I18nService.translate("PGS_NPC_KILLS")} field="PGS_NPC_KILLS" />
                ]}/>
    );
};

export default NpcKillPage;
