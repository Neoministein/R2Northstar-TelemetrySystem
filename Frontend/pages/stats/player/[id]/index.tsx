import {useRouter} from "next/router";
import useQuery from "../../../../src/utils/useQuery";
import {useEffect, useState} from "react";
import PlayerLookUpService, {PlayerLookUp} from "../../../../src/service/PlayerLookUpService";
import {primeToast} from "../../../../layout/AppTopbar";

const PlayerStatsPage = () => {
    const [playerLookUp, setPlayerLookUp] = useState<PlayerLookUp>();
    const router = useRouter();
    const query = useQuery();


    useEffect(() => {
        if (!query) {
            return;
        }

        PlayerLookUpService.getPlayerLookupFromName(query.id as string)
            .then(result => {
                setPlayerLookUp(result)
            }).catch( ex => {
                primeToast.show({ severity: 'error', summary: 'Player not found', detail: 'The player "' + query.id + '" does not exists', life: 3000 });
                router.push('/stats/player')
            })
    },[query])

    return (
        <div className="grid">
            <div className="col-12">
                <div className="card">
                    <h1>{playerLookUp?.playerName}</h1>
                </div>
            </div>
        </div>
    );
};

export default PlayerStatsPage;

