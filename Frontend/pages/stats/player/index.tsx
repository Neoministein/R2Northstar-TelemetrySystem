import {useState} from "react";
import {AutoComplete} from "primereact/autocomplete";
import PlayerLookUpService, {PlayerLookUp} from "../../../src/service/PlayerLookUpService";
import CssUtils from "../../../src/utils/CssUtils";
import {useRouter} from "next/router";

const PlayerSearchPage = () => {

    const router = useRouter();

    const [selectedPlayers, setSelectedPlayers] = useState<PlayerLookUp | string>(null);
    const [filteredCountries, setFilteredPlayers] = useState<PlayerLookUp[]>([]);

     const search = async (event) => {
         setFilteredPlayers((await PlayerLookUpService.searchPlayerLookupFromName(event.query)));
    }

    const conversionMethod = (item: PlayerLookUp) => {
        return highlightSubstring(item.playerName, selectedPlayers)
    };

     const highlightSubstring = (s1: string, s2: any) => {
         if (typeof s2 === "string") {
             const startIndex = s1.toLowerCase().indexOf((s2).toLowerCase());

             if (startIndex !== -1) {
                 const endIndex = startIndex + (s2).length;

                 return (
                     <span>
                         {s1.substring(0, startIndex)}
                         <span style={{
                             fontWeight: 900,
                             letterSpacing: '1px',
                             textShadow: '1px 0 ' + CssUtils.getPrimaryColor(document.documentElement)}}>
                             {s1.substring(startIndex, endIndex)}
                         </span>
                         {s1.substring(endIndex)}
                     </span>
                 );
             }
         }
         return s1; // s2 not found in s1, return s1 as is
    }

    return (
        <div className="grid">
            <div className="col-12">
                <div className="card">
                    <AutoComplete
                        field="playerName"
                        value={selectedPlayers}
                        placeholder="Enter Titanfall Username"
                        forceSelection={true}
                        minLength={3}
                        autoFocus={true}
                        autoHighlight={true}
                        suggestions={filteredCountries}
                        completeMethod={search}
                        itemTemplate={conversionMethod}
                        onChange={(e) => setSelectedPlayers(e.value)}
                        onSelect={(selectEvent) => router.push('/stats/player/[id]', '/stats/player/' + selectEvent.value.playerName )}
                        style={{width:'50%'}}/>
                </div>
            </div>
        </div>
    );
}

export default PlayerSearchPage;
