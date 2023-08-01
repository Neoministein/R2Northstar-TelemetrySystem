import {Column} from 'primereact/column';
import StatsService, {PlayerKdBucket} from '../../../../../src/service/StatsService';
import TopResultTableV2 from "../../../../../src/components/TopResultTableV2";
import I18nService from "../../../../../src/service/I18nService";

const WinRatioPage = () => {

    const playerKd = (column: PlayerKdBucket) => {
        if(column?.kd) {
            return column?.kd.toFixed(3);
        }
        return null;
    }

    return (
        <TopResultTableV2
            title="Top Player K/D"
            getGlobalRanking={(tags) => StatsService.getTopPlayerKd(tags)}
            columns={
                [
                    <Column header={I18nService.translate("playerName")}        field="playerName" />,
                    <Column header={I18nService.translate("K/D")}               field="kd"                  body={playerKd} />,
                    <Column header={I18nService.translate("PGS_PILOT_KILLS")}   field="PGS_PILOT_KILLS" />,
                    <Column header={I18nService.translate("PGS_DEATHS")}        field="PGS_DEATHS" />]}/>
    );
};

export default WinRatioPage;
