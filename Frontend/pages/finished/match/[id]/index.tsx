import {useRouter} from 'next/router';
import * as React from "react";
import {useEffect, useState} from "react";
import useQuery from "../../../../src/utils/useQuery";
import MatchService, {MatchEntity} from "../../../../src/service/MatchService";
import Heatmap from "../../../../src/components/Heatmap";
import {HeatmapEntity} from "../../../../src/service/MapService";
import {retry} from "../../../../src/utils/retry";

const FinishedMatchPage = () => {

    const router = useRouter();
    const query = useQuery();
    const [match, setMatch] = useState<MatchEntity>();
    const [heatmap, setHeatmap] = useState<HeatmapEntity>();

    useEffect(() => {
        if (!query) {
            return;
        }
        MatchService.getMatch(query.id as string)
            .then( result => setMatch(result))
            .catch( ex => {
                // @ts-ignore
                window.PrimeToast.show({ severity: 'error', summary: 'Match not found', detail: `The match that you tried to navigate to does not exist:`, life: 3000 });
                router.push('/live/match')
            });

        const a = () => MatchService.getHeatmap(query.id as string)
            .then( result => {
                if (result.status === "FINISHED") {
                    console.log("S")
                    setHeatmap(result)
                } else {
                    console.log("E")
                    throw Error();
                }
            })
        retry(a, { retries: 6, retryIntervalMs: 10_000 })

    },[query])


    return (
        <div>
            <h2>{match?.nsServerName}</h2>
            {
                heatmap != null && match != null ? (
                    <Heatmap heatmap={heatmap} map={match.mapDetails}/>
                    ) :
                    <div/>
            }
        </div>
    );
};

export default FinishedMatchPage;

