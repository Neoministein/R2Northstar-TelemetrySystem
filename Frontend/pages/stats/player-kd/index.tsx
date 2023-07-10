import {Column} from 'primereact/column';
import StatsService, {PlayerKdBucket} from '../../../src/service/StatsService';
import TopResultTable from '../../../src/components/TopResultTable';

const WinRatioPage = () => {

    const playerKd = (column: PlayerKdBucket) => {
        if(column?.kd) {
            return column?.kd.toFixed(3);
        }
        return null;
    }

    return (
        <TopResultTable
            title="Top Player K/D"
            getGlobalRanking={(tags) => StatsService.getTopPlayerKd(tags)}
            columns={
                [<Column header="Player Name" field="playerName" />,
                <Column header="K/D" body={playerKd} />,
                <Column header="Kills"  field="PGS_PILOT_KILLS" />,
                <Column header="Deaths" field="PGS_DEATHS" />]}/>
    );
};

export default WinRatioPage;
