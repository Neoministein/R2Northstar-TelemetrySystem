import {Chart} from "primereact/chart";
import {useEffect, useState} from "react";

const Dashboard = () => {

    let totalUniquePlayers = 152;
    let playersLast24Hours = 10;

    let totalMatches = 22;
    let matchesLast24Hours = 3;

    let playerKills = 753;
    let npcKills = 1235;

    let distanceTraveled = 45;
    let distanceTraveledLast24Hours = 10;

    const [chartData, setChartData] = useState({});
    const [chartOptions, setChartOptions] = useState({});

    useEffect(() => {
        const documentStyle = getComputedStyle(document.documentElement);
        const data = {
            labels: ['A', 'B', 'C'],
            datasets: [
                {
                    data: [540, 325, 702],
                    backgroundColor: [
                        documentStyle.getPropertyValue('--blue-500'),
                        documentStyle.getPropertyValue('--yellow-500'),
                        documentStyle.getPropertyValue('--green-500')
                    ],
                    hoverBackgroundColor: [
                        documentStyle.getPropertyValue('--blue-400'),
                        documentStyle.getPropertyValue('--yellow-400'),
                        documentStyle.getPropertyValue('--green-400')
                    ]
                }
            ]
        }
        const options = {
            plugins: {
                legend: {
                    labels: {
                        usePointStyle: true
                    }
                }
            }
        };

        setChartData(data);
        setChartOptions(options);
    }, []);

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
                    <h5>Live Kill feed</h5>

                </div>
            </div>
            <div className="col-12 lg:col-6 xl:col-6">
                <div className="card flex flex-column align-items-center">
                    <h5>Gamemode Distribution</h5>
                    <Chart type="pie" data={chartData} options={chartOptions} className="w-full md:w-30rem" style={{}} />
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
