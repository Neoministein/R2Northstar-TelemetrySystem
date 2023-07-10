import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTable from '../../../src/components/TopResultTable';

const WinsPage = () => {

    return (
        <TopResultTable
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopWins(tags)}
            columns={
                [<Column header="Player Name" field="playerName"/>,
                <Column header="Wins" field="win" />]}/>
    );
};

export default WinsPage;
