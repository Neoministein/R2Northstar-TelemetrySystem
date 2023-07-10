import {Button} from "primereact/button";
import {Card} from "primereact/card"
import {DataTable} from "primereact/datatable";
import {Skeleton} from "primereact/skeleton";
import {VirtualScrollerLazyEvent, VirtualScrollerLoadingTemplateOptions} from "primereact/virtualscroller";
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

export default function TopResultTable<T extends PlayerBucket>({getGlobalRanking, columns, title} : TableProps) {
    const tableSteps = 50;

    const [globalRanking, setGlobalRanking] = useState<T[]>([]);

    const [virtualRanking, setVirtualRanking] = useState<T[]>([]);
    const [virtualState, setVirtualState] = useState({
        tags: [] as string[],
        requireRefresh: true
    });
    const [lazyLoading, setLazyLoading] = useState<boolean>(false);
    const [showAdanced, setShowAdanced] = useState<boolean>();

    useEffect(() => {
        if (virtualState.requireRefresh) {
            loadRankingLazy({first: 0, last: tableSteps} as VirtualScrollerLazyEvent);
        }
    }, [virtualState]);

    const loadRankingLazy = async (event: VirtualScrollerLazyEvent) => {
        !lazyLoading && setLazyLoading(true);

        const first = event.first as number;
        const last = event.last as number;

        let _virtualRanking;
        let _globalRanking;


        if(virtualState.requireRefresh) {
            _globalRanking = await getGlobalRanking(virtualState.tags);

            setGlobalRanking(_globalRanking);
            _virtualRanking = Array.from({ length: _globalRanking.length});

        } else {
            _virtualRanking = [...virtualRanking];
            _globalRanking = globalRanking;
        }


        const loadedRanking =  _globalRanking.slice(first as number, last as number);
        const playerNameObject = await PlayerLookUpService.getPlayerNamesFromUid(loadedRanking.map(row => row.key));
        loadedRanking.forEach(element => {
            element.playerName = playerNameObject[element.key]
        });
        Array.prototype.splice.apply(_virtualRanking, [...[first, last as number - (first as number)], ...loadedRanking]);
        setVirtualRanking(_virtualRanking);
        setVirtualState(object => {return{ tags: object.tags, requireRefresh: false}});
        setLazyLoading(false);
    };

    const loadingTemplate = (options: VirtualScrollerLoadingTemplateOptions) => {
        return (
            <div className="flex align-items-center" style={{ height: '17px', flexGrow: '1', overflow: 'hidden' }}>
                <Skeleton width={options.cellEven ? (options.field === 'year' ? '30%' : '40%') : '60%'} height="1rem" />
            </div>
        );
    };

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
                            icon={showAdanced ? 'pi pi-minus' : 'pi pi-plus'}
                            label="Advanced"
                            onClick={() => setShowAdanced(!showAdanced)}
                            className="p-button-text"
                            style={{ marginLeft: "auto" }}/>
                    </h5>

                    {showAdanced ?
                                <Card title="Advanced Settings">
                                    <InputTextTags lable="Server Tags" onChange={updateValues}/>
                                </Card>
                    : null}

                    <div style={{height: "100%", paddingBottom: "30px"}}>
                        <DataTable
                            value={virtualRanking}
                            scrollable
                            scrollHeight={"flex"}
                            /*style={{paddingBottom:  showAdanced ? "175px" : "30px"}}*/
                            tableStyle={{ minWidth: '10rem' }} key={"key"}
                            virtualScrollerOptions={{
                                id: "dadada",
                                lazy: true,
                                onLazyLoad: loadRankingLazy,
                                itemSize: 46,
                                delay: 0,
                                showLoader: true,
                                loading: lazyLoading,
                                loadingTemplate,
                                step: tableSteps}}>


                                <Column header="Rank" body={(data, props) => (props.rowIndex + 1) + "."}/>
                                {columns}
                        </DataTable>
                    </div>
                </div>
            </div>
        </div>
    );
}
