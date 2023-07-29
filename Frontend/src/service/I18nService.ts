const map = new Map([
    ["playerName", "Player Name"],
    ["PGS_ELIMINATED", "Eliminations"],
    ["PGS_KILLS", "Kills"],
    ["PGS_DEATHS", "Deaths"],
    ["PGS_PILOT_KILLS", "Pilot Kills"],
    ["PGS_TITAN_KILLS", "Titan Kills"],
    ["PGS_NPC_KILLS", "Npc Kills"],
    ["PGS_ASSISTS", "Assists"],
    ["PGS_SCORE", "Score"],
    ["PGS_ASSAULT_SCORE", "Assault Score"],
    ["PGS_DEFENSE_SCORE", "Defense Score"],
    ["PGS_DISTANCE_SCORE", "Distance Score"],
    ["PGS_DETONATION_SCORE", "Detonation Score"]
]);

const I18nService = {





    translate(key: string) :string{
        return map.get(key);
    }
}

export default I18nService;
