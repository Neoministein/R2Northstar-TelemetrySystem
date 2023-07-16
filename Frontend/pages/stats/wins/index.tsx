import {Column} from 'primereact/column';
import StatsService from '../../../src/service/StatsService';
import TopResultTableV2 from "../../../src/components/TopResultTableV2";

const WinsPage = () => {

    return (
        <TopResultTableV2
            title="Top Player Kills"
            getGlobalRanking={(tags) => StatsService.getTopWins(tags)}
            columns={
                [<Column header="Player Name" field="playerName"/>,
                <Column header="Wins" field="win" />]}/>
    );
};

export default WinsPage;
