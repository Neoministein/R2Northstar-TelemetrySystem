const map = new Map([
    //Stats
    ["PGS_ELIMINATED",          "Eliminations"],
    ["PGS_KILLS",               "Kills"],
    ["PGS_DEATHS",              "Deaths"],
    ["PGS_PILOT_KILLS",         "Pilot Kills"],
    ["PGS_TITAN_KILLS",         "Titan Kills"],
    ["PGS_NPC_KILLS",           "Npc Kills"],
    ["PGS_ASSISTS",             "Assists"],
    ["PGS_SCORE",               "Score"],
    ["PGS_ASSAULT_SCORE",       "Assault Score"],
    ["PGS_DEFENSE_SCORE",       "Defense Score"],
    ["PGS_DISTANCE_SCORE",      "Distance Score"],
    ["PGS_DETONATION_SCORE",    "Detonation Score"],
    //Maps
    ["mp_angel_city",           "Angel City"],
    ["mp_black_water_canal",    "Black Water Canal"],
    ["mp_coliseum",             "Coliseum"],
    ["mp_coliseum_column",      "Coliseum Column"],
    ["mp_colony02",             "Colony"],
    ["mp_complex3",             "Complex"],
    ["mp_crashsite3",           "Crashsite"],
    ["mp_drydock",              "Drydock"],
    ["mp_eden",                 "Eden"],
    ["mp_forwardbase_kodai",    "Forwardbase Kodai"],
    ["mp_glitch",               "Glitch"],
    ["mp_grave",                "Boom Town"],
    ["mp_homestead",            "Homestead"],
    ["mp_lf_deck",              "Deck"],
    ["mp_lf_meadow",            "Meadow"],
    ["mp_lf_stacks",            "Stacks"],
    ["mp_lf_township",          "Township"],
    ["mp_lf_traffic",           "Traffic"],
    ["mp_lf_uma",               "Uma"],
    ["mp_relic02",              "Relic"],
    ["mp_rise",                 "Rise"],
    ["mp_thaw",                 "Exoplanet"],
    ["mp_wargames",             "Wargames"],
    //Gamemodes
    ["private_match",           "Private Match"],
    ["aitdm",                   "Attrition"],
    ["at",                      "Bounty Hunt"],
    ["coliseum",                "Coliseum"],
    ["cp",                      "Amped Hardpoint"],
    ["ctf",                     "Capture the Flag"],
    ["fd_easy",                 "Frontier Defense (Easy)"],
    ["fd_hard",                 "Frontier Defense (Hard)"],
    ["fd_insane",               "Frontier Defense (Insane)"],
    ["fd_master",               "Frontier Defense (Master)"],
    ["fd_normal",               "Frontier Defense (Regular)"],
    ["fw",                      "Frontier War"],
    ["lts",                     "Last Titan Standing"],
    ["mfd",                     "Marked For Death"],
    ["ps",                      "Pilots vs. Pilots"],
    ["solo",                    "Campaign"],
    ["tdm",                     "Skirmish"],
    ["ttdm",                    "Titan Brawl"],
    //Site
    ["playerName",              "Player Name"],
    ["ratio",                   "Ratio"],
    ["wins",                    "Wins"],
    ["loses",                   "Loses"],
    ["K/D",                     "K/D"]
]);

const I18nService = {

    translate(key: string) :string {
        const translation = map.get(key);
        if (translation) {
            return translation;
        }
        return "i18n." + key;
    }
}

export default I18nService;
