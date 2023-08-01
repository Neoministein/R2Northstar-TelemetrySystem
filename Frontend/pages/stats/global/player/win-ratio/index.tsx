import {Column} from 'primereact/column';
import StatsService, {WinRatioBucket} from '../../../../../src/service/StatsService';
import TopResultTableV2 from "../../../../../src/components/TopResultTableV2";
import I18nService from "../../../../../src/service/I18nService";

const ButtonDemo = () => {

    const winRatioBody = (column: WinRatioBucket) => {
        if(column?.ratio) {
            return column.ratio.toFixed(3) + "%";
        }
        return null;
    }

    return (
        <TopResultTableV2
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopWinRatio(tags)}
            columns={
                [
                    <Column header={I18nService.translate("playerName")}    field="playerName" />,
                    <Column header={I18nService.translate("ratio")}         field="ratio"   body={winRatioBody} />,
                    <Column header={I18nService.translate("wins")}          field="filters.win" />,
                    <Column header={I18nService.translate("loses")}         field="filters.lose" />]}/>
    );
};

export default ButtonDemo;
