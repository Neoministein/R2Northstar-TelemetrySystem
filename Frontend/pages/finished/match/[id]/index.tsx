import {useRouter} from 'next/router';
import * as React from "react";
import {useEffect, useRef, useState} from "react";
import useQuery from "../../../../src/utils/useQuery";
import MatchService, {MatchEntity} from "../../../../src/service/MatchService";
import Heatmap from "../../../../src/components/Heatmap";
import {HeatmapEntity} from "../../../../src/service/MapService";
import {retry} from "../../../../src/utils/retry";
import {primeToast} from "../../../../layout/AppTopbar";
import ErrorUtils from "../../../../src/utils/ErrorUtils";
import {Card} from "primereact/card";
import {ProgressSpinner} from "primereact/progressspinner";
import MatchResultTable from "../../../../src/components/MatchResultTable";

const FinishedMatchPage = () => {

    const router = useRouter();
    const query = useQuery();
    const [match, setMatch] = useState<MatchEntity>();
    const [heatmap, setHeatmap] = useState<HeatmapEntity>();
    const [heatmapFailed, setHeatmapFailed] = useState<boolean>(false)
    const heatmapParent = useRef();

    useEffect(() => {
        if (!query) {
            return;
        }
        MatchService.getMatch(query.id as string)
            .then( result => setMatch(result))
            .catch( () => {
                primeToast.show({ severity: 'error', summary: 'Match not found', detail: 'The match that you tried to navigate to does not exist', life: 3000 });
                router.push('/live/match')
            });

        const getHeatmap = () => MatchService.getHeatmap(query.id as string)
            .then( result => {
                if (result.status === "FINISHED") {
                    setHeatmap(result)
                } else if (result.status === "FAILED") {
                    throw Error("Heatmap generation failed");
                } else {
                    throw Error("The server is still processing try again later");
                }
            })
        retry(getHeatmap, { retries: 6, retryIntervalMs: 10_000 })
            .catch(ex => {
                setHeatmapFailed(true)
                ErrorUtils.displayError(ex)
            });
    },[query])


    return (
        <div>
            <Card title={match?.nsServerName}>
                <div className="grid">
                    <div className="md:col-7 sm:col-12" ref={heatmapParent}>
                        {
                            heatmapFailed
                                ?
                            <div>
                                Cannot load heatmap
                            </div>
                            :
                            <div>
                                {
                                    heatmap != null && match != null ? (
                                            <Heatmap heatmap={heatmap} map={match.mapDetails} parentObject={heatmapParent}/>
                                        ) :
                                        <div>
                                            <div>
                                                Waiting for the heatmap to be generated...
                                            </div>
                                            <ProgressSpinner/>
                                        </div>

                                }
                            </div>
                        }
                    </div>

                    <div className="md:col-5 sm:col-12">
                        <h2 style={{paddingLeft: '10px'}}>Scoreboard</h2>
                        {
                            match?.id != null ? <MatchResultTable matchId={match.id}/> : null
                        }
                    </div>
                </div>
            </Card>
        </div>
    );
};

export default FinishedMatchPage;

