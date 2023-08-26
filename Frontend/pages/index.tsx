import {Chart} from "primereact/chart";
import {useEffect, useState} from "react";
import LiveFeed from "../src/components/LiveFeed";
import I18nService from "../src/service/I18nService";
import DashboardService, {GameModeDistribution} from "../src/service/DashboardService";

const Dashboard = () => {

    let totalUniquePlayers = 152;
    let playersLast24Hours = 10;

    let totalMatches = 22;
    let matchesLast24Hours = 3;

    let playerKills = 753;
    let npcKills = 1235;

    let distanceTraveled = 45;
    let distanceTraveledLast24Hours = 10;

    const [distribution, setDistribution] = useState<GameModeDistribution[]>([])

    useEffect(() => {
        DashboardService.getModeDistribution().then(response => {setDistribution(response)})
    }, []);

    function getChartData() {
        if (distribution.length == 0) {
            return {}
        }
        return {
            labels: distribution.map(v => I18nService.translate(v.mode)),
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
        }
    }

    return (
        <div className="grid">
            <div className="col-12 card">
                <h1 className="tf2-font-link" style={{textAlign:'center'}}>Global R2-Northstar Telemetry</h1>
            </div>
            <div className="col-12 lg:col-6 xl:col-3">
                <div className="card">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">Total Unique Players</span>
                            <div className="text-900 font-medium text-xl">{totalUniquePlayers}</div>
                        </div>
                        <div
                            className="flex align-items-center justify-content-center bg-blue-100 border-round"
                            style={{ width: "2.5rem", height: "2.5rem" }}
                        >
                            <i className="pi pi-shopping-cart text-blue-500 text-xl" />
                        </div>
                    </div>
                    <span className="text-green-500 font-medium">{playersLast24Hours}</span>
                    <span className="text-500"> | In the last 24 hours </span>
                </div>

            </div>
            <div className="col-12 lg:col-6 xl:col-3">
                <div className="card">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">Player Kills Last 24 hours</span>
                            <div className="text-900 font-medium text-xl">{playerKills}</div>
                        </div>
                        <div
                            className="flex align-items-center justify-content-center bg-blue-100 border-round"
                            style={{ width: "2.5rem", height: "2.5rem" }}
                        >
                            <i className="pi pi-shopping-cart text-blue-500 text-xl" />
                        </div>
                    </div>
                    <span className="text-green-500 font-medium">{npcKills}</span>
                    <span className="text-500"> | Npc Kills Last 24 hours </span>
                </div>

            </div>
            <div className="col-12 lg:col-6 xl:col-3">
                <div className="card">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">Total Matches</span>
                            <div className="text-900 font-medium text-xl">{totalMatches}</div>
                        </div>
                        <div
                            className="flex align-items-center justify-content-center bg-blue-100 border-round"
                            style={{ width: "2.5rem", height: "2.5rem" }}>
                            <i className="pi pi-shopping-cart text-blue-500 text-xl" />
                        </div>
                    </div>
                    <span className="text-green-500 font-medium">{matchesLast24Hours}</span>
                    <span className="text-500"> | In the last 24 hours </span>
                </div>
            </div>
            <div className="col-12 lg:col-6 xl:col-3">
                <div className="card">
                    <div className="flex justify-content-between mb-3">
                        <div>
                            <span className="block text-500 font-medium mb-3">Distance Traveled</span>
                            <div className="text-900 font-medium text-xl">{distanceTraveled}km</div>
                        </div>
                        <div
                            className="flex align-items-center justify-content-center bg-blue-100 border-round"
                            style={{ width: "2.5rem", height: "2.5rem" }}>
                            <i className="pi pi-shopping-cart text-blue-500 text-xl" />
                        </div>
                    </div>
                    <span className="text-green-500 font-medium">{distanceTraveledLast24Hours}km</span>
                    <span className="text-500"> | In the last 24 hours </span>
                </div>
            </div>
            <div className="col-12 lg:col-6 xl:col-6">
                <div className="card">
                    <LiveFeed maxSize={10} feedName="player-kills" renderItem={(item) =>
                        <div>
                            {item.title} with [{I18nService.translate(item.contentText)}]
                        </div>
                    }/>
                </div>
            </div>
            <div className="col-12 lg:col-6 xl:col-6">
                <div className="card flex flex-column align-items-center">
                    <h5>Gamemode Distribution</h5>
                    <Chart type="pie" data={getChartData()} options={getChartOptions()} className="w-full md:w-30rem" />
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
