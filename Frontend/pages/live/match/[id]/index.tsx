import {useRouter} from 'next/router';
import * as React from "react";
import {useEffect, useRef, useState} from "react";
import MatchService, {MatchEntity} from "../../../../src/service/MatchService";
import useQuery from "../../../../src/utils/useQuery";
import {primeToast} from "../../../../layout/AppTopbar";
import {Card} from "primereact/card";
import Minimap from "../../../../src/components/Minimap";

const LiveMatchPage = () => {

    const router = useRouter();
    const query = useQuery();
    const [match, setMatch] = useState<MatchEntity>(undefined);
    const parentObject = useRef();

    useEffect(() => {
        if (!query) {
            return;
        }
        MatchService.getMatch(query.id as string)
            .then( result => setMatch(result))
            .catch( ex => {
                primeToast.show({ severity: 'error', summary: 'Match not found', detail: `The match that you tried to navigate to does not exist:`, life: 3000 });
                router.push('/live/match')
            })

    },[query])

    return (
        <div>
            <Card ref={parentObject} title={match?.nsServerName} style={{height: 'calc(100vh - 9.5rem)', display: 'flex', flexDirection: 'column'}}>
                {
                    match != null ? <Minimap match={match} parentObject={parentObject}/> : null
                }
            </Card>
        </div>

    );
};

export default LiveMatchPage;

