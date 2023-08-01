import {Column} from 'primereact/column';
import StatsService from '../../../../../src/service/StatsService';
import TopResultTableV2 from "../../../../../src/components/TopResultTableV2";
import I18nService from "../../../../../src/service/I18nService";

const WinsPage = () => {

    return (
        <TopResultTableV2
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopWins(tags)}
            columns={
                [
                    <Column header={I18nService.translate("playerName")} field="playerName"/>,
                    <Column header={I18nService.translate("wins")} field="win" />]}/>
    );
};

export default WinsPage;
