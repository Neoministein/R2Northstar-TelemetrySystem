import {useEffect, useState} from "react";
import {useRouter} from "next/router";
import I18nService from "../../../../src/service/I18nService";
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";
import MapService, {GameMap, MapDistribution} from "../../../../src/service/MapService";
import {Chart} from "primereact/chart";

const MapStatsListPage = () => {
    const [gameMap, setGameMap] = useState<GameMap[]>([]);
    const [distribution, setDistribution] = useState<MapDistribution[]>([])
    const [loading, setLoading] = useState<boolean>(true);
    const router = useRouter();

    const [chartClicked, setChartClicked] = useState<number>(-1);
    const [chartOptions, setChartOptions] = useState({});

    useEffect(() => {
        MapService.getAllMapDetails()
            .then(data => setGameMap(data))
            .then(() => setLoading(false));
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const handleClick = (mapName : any) => {
        router.push('/stats/global/map/[id]', '/stats/global/map/' + mapName );
    };

    useEffect(() => {
        MapService.getMapDistribution().then(response => {setDistribution(response)})
    }, []);

    useEffect(() => {
        if (chartClicked !== -1) {
            handleClick(distribution[chartClicked].map)
        }
    }, [chartClicked])

    function getChartData() {
        if (distribution.length == 0) {
            return {}
        }
        return {
            labels: distribution.map(v => I18nService.translate(v.map)),
            datasets: [
                {
                    data: distribution.map(v => v.percent),
                    backgroundColor: [
                        '#42a5f5',
                        '#5c6bc0',
                        '#7e57c2',
                        '#ab47bc',
                        '#ec407a',
                        '#ef5350',
                        '#d4e157',
                        '#9ccc65',
                        '#66bb6a',
                        '#26a69a',
                        '#26c6da',
                        '#42a5f5',
                        '#8d6e63',
                        '#ff7043',
                        '#ffa726',
                        '#ffca28',
                        '#ffee58',
                        '#d4e157',
                    ]
                }
            ]
        };
    }

    function getChartOptions() {
        return {
            options: '60%',
            onClick: function(evt, element) {
                if (element.length > 0) {
                    setChartClicked(element[0].index)
                }
            }
        }
    }

    return (
        <div className="grid">
            <div className="col-12">
                <div className="card">
                    <div className="flex justify-content-center">
                        <Chart type="doughnut" data={getChartData()} options={getChartOptions()} className="w-full md:w-30rem" />
                    </div>
                    <DataTable value={gameMap} loading={loading} scrollable={true} selectionMode="single" onSelectionChange={e => {
                        // @ts-ignore
                        handleClick(e.value.name);
                    }}>
                        <Column header="Map" field="name" body={rowData => I18nService.translate(rowData.name)}/>
                    </DataTable>
                </div>
            </div>
        </div>
    );
}

export default MapStatsListPage;
