import {Button} from "primereact/button";
import {Card} from "primereact/card"
import {DataTable} from "primereact/datatable";
import {useEffect, useState} from "react";
import {PlayerBucket} from "../service/StatsService";
import InputTextTags from "./InputTextTags";
import {Column} from "primereact/column";
import PlayerLookUpService from "../service/PlayerLookUpService";

interface TableProps {
    getGlobalRanking(tags: string[]): Promise<any>
    columns: JSX.Element[]
    title: string
}

export default function TopResultTableV2<T extends PlayerBucket>({getGlobalRanking, columns, title} : TableProps) {
    const [globalRanking, setGlobalRanking] = useState<T[]>([]);
    const [virtualState, setVirtualState] = useState({
        tags: [] as string[],
        requireRefresh: true
    });
    const [showAdvanced, setShowAdvanced] = useState<boolean>();
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        getGlobalRanking(virtualState.tags)
            .then(async result => {
                const playerNameObject = await PlayerLookUpService.getPlayerNamesFromUid(result.map(row => row.key));
                result.forEach(element => {
                    element.playerName = playerNameObject[element.key]
                });
                return result
            })
            .then(result => setGlobalRanking(result))
            .then(() => setLoading(false))
    }, [virtualState]);

    const updateValues = (newTags : string[]) => {
        setVirtualState({ tags: newTags, requireRefresh: true});
    }

    return (
        /* TODO FIX LAYOUT AT SOME TIME */
        <div className="grid">
            <div className="col-12">
                <div className="card" style={{height: "calc(100vh - 9.5rem)", overflow: "hidden"}}>
                    <h5 style={{display: "flex" }}>
                        {title}
                        <Button
                            icon={showAdvanced ? 'pi pi-minus' : 'pi pi-plus'}
                            label="Advanced"
                            onClick={() => setShowAdvanced(!showAdvanced)}
                            className="p-button-text"
                            style={{ marginLeft: "auto" }}/>
                    </h5>

                    {showAdvanced ?
                                <Card title="Advanced Settings">
                                    <InputTextTags lable="Server Tags" onChange={updateValues}/>
                                </Card>
                    : null}

                    <div style={{height: "100%", paddingBottom: "30px"}}>
                        <DataTable
                            value={globalRanking}
                            scrollable={true}
                            scrollHeight={"flex"}
                            loading={loading}
                            /*style={{paddingBottom:  showAdanced ? "175px" : "30px"}}*/
                            tableStyle={{ minWidth: '10rem' }} key={"key"}>
                                <Column header="Rank" body={(data, props) => (props.rowIndex + 1) + "."}/>
                                {columns}
                        </DataTable>
                    </div>
                </div>
            </div>
        </div>
    );
}
