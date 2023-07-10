import {Column} from 'primereact/column';
import StatsService, {WinRatioBucket} from '../../../src/service/StatsService';
import TopResultTable from '../../../src/components/TopResultTable';

const ButtonDemo = () => {

    const winRatioBody = (column: WinRatioBucket) => {
        if(column?.ratio) {
            return column.ratio + "%";
        }
        return null;
    }

    return (
        <TopResultTable
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopWinRatio(tags)}
            columns={
                [<Column header="Player Name" field="playerName" />,
                <Column header="Ratio" body={winRatioBody} />,
                <Column header="Wins"  field="filters.win" />,
                <Column header="Loses" field="filters.lose" />]}/>
    );
};

export default ButtonDemo;
