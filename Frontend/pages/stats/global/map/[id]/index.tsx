import {useRouter} from 'next/router';
import * as React from "react";
import {useEffect, useRef, useState} from "react";
import {Card} from "primereact/card";
import MapService, {GameMap, HeatmapEntity} from "../../../../../src/service/MapService";
import useQuery from "../../../../../src/utils/useQuery";
import {primeToast} from "../../../../../layout/AppTopbar";
import I18nService from "../../../../../src/service/I18nService";
import Heatmap from "../../../../../src/components/Heatmap";

const MapStatsPage = () => {

    const router = useRouter();
    const query = useQuery();
    const [map, setMap] = useState<GameMap>();
    const [heatmap, setHeatmap] = useState<HeatmapEntity>();
    const parentObject = useRef();

    useEffect(() => {
        if (!query) {
            return;
        }
        MapService.getMapDetails(query.id as string)
            .then( result => setMap(result))
            .catch( ex => {
                primeToast.show({ severity: 'error', summary: 'Map not found', detail: `The map does not exists`, life: 3000 });
                router.push('/stats/global/map')
            })
        MapService.getMapHeatmap(query.id as string)
            .then(result => setHeatmap(result))

    },[query])

    return (
        <div>
            <Card title={I18nService.translate(map?.name)} style={{minHeight: 'calc(100vh - 9.5rem)', display: 'flex', flexDirection: 'column'}}>
                <div className="grid">
                    <div className="md:col-offset-3 md:col-6 sm:col-12" ref={parentObject}>
                        {heatmap != null ? <Heatmap heatmap={heatmap} map={map} parentObject={parentObject}/> : null}
                    </div>
                    <div className="col-12">

                    </div>
                </div>
            </Card>
        </div>

    );
};

export default MapStatsPage;

