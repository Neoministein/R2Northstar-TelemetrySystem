import StatsService, {MatchResult} from "../service/StatsService";
import {useEffect, useState} from "react";
import PlayerLookUpService from "../service/PlayerLookUpService";
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";
import I18nService from "../service/I18nService";

interface MatchResultTableProps {
    matchId: string
}

export default function MatchResultTable({matchId} : MatchResultTableProps) {
    const [matchResult, setMatchResult] = useState<MatchResult[]>(undefined);
    const [columnsToRender, setColumnsToRender] = useState({
        playerName: true,
        PGS_ELIMINATED: false,
        PGS_KILLS: false,
        PGS_DEATHS: false,
        PGS_PILOT_KILLS: false,
        PGS_TITAN_KILLS: false,
        PGS_NPC_KILLS: false,
        PGS_ASSISTS: false,
        PGS_SCORE: false,
        PGS_ASSAULT_SCORE: false,
        PGS_DEFENSE_SCORE: false,
        PGS_DISTANCE_SCORE: false,
        PGS_DETONATION_SCORE: false,
    });

    useEffect(() => {
        StatsService.getMatchResult(matchId)
            .then(async result => {
                const playerNameObject = await PlayerLookUpService.getPlayerNamesFromUid(result.map(row => row.uId));
                result.forEach(element => {
                    element.playerName = playerNameObject[element.uId]
                });
                return result
            })
            .then(result => {
                const newVal = columnsToRender;

                Object.keys(newVal).forEach(key => {
                    const val = newVal[key];
                    if (!val) {
                        const valFound = result.find( row => {
                            const rowKey = row[key];
                            if (rowKey != null ||rowKey != undefined) {
                                return true;
                            } else {
                                return false;
                            }
                        })

                        newVal[key] = valFound != undefined || valFound != null
                    }
                });
                setColumnsToRender(newVal)
                setMatchResult(result)
            })


    }, []);

    return (
        <DataTable
            value={matchResult}
            scrollable={true}
            scrollHeight={"flex"}
            loading={matchResult == undefined}
            tableStyle={{ minWidth: '10rem' }} key={"key"}>

            {
                Object.keys(columnsToRender).map((key) => {
                    if (columnsToRender[key]) {
                        return (<Column key={key} field={key} header={ I18nService.translate(key) }/>)
                    } else {
                        return null
                    }
                })
            }


        </DataTable>
    )
}
