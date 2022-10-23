using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CSNamedPipeServer
{
    public class Match
    {
        /// <summary>
        /// Name of the current map
        /// </summary>
        public string mapName;
        /// <summary>
        /// Unique id of the match
        /// </summary>
        public string matchId = "4bd21f79-9394-481f-b060-7644335b87fb"; // 36 Stellen auch mit Bindestrichen
        /// <summary>
        /// Gamemode short term
        /// </summary>
        private string gamemode;
        /// <summary>
        /// Name of the server - same name as in ingame server browser
        /// </summary>
        private string serverName;
        /// <summary>
        /// Dictionary of all players currently on the server
        /// </summary>
        public Dictionary<string, Player> players = new Dictionary<string, Player>();
        /// <summary>
        /// Whether this match is currently running
        /// </summary>
        public bool isRunning = false;

        /// <summary>
        /// Creates a new match
        /// </summary>
        /// <param name="_mapName">Name of map</param>
        /// <param name="_matchId">Id of map given by the backend</param>
        /// <param name="_serverName">Name of the northstar server</param>
        /// <param name="_dontAskServer">Backend wont be notified - for initial named pipe connection</param>
        public Match(string _mapName, string _gamemode, string _serverName, bool _startImmediatly = true, bool _dontAskServer = false)
        {
            mapName = _mapName;
            gamemode = _gamemode;
            if (!_dontAskServer)
            {
                Task t1 = Task.Run(() => AssignMatchId(_mapName, _gamemode, _serverName).Wait());
                isRunning = _startImmediatly;
            }
        }

        /// <summary>
        /// Assins a match id
        /// </summary>
        /// <param name="_mapName">Name of the current map</param>
        /// <param name="_gamemode">Gamemode short term</param>
        /// <returns></returns>
        /// <exception cref="WrongAnswerException">Thrown when the data is for a different server</exception>
        public async Task AssignMatchId(string _mapName, string _gamemode, string _serverName)
        {
            NewMatchRequest newResponse = new NewMatchRequest() { map = _mapName, ns_server_name = _serverName, gamemode = _gamemode };
            if (PipeReader.argUseHttp)
            {
                string answer = await Output.PostJsonHttpClient(GloVars.ArgUrl + "/match/new", JsonConvert.SerializeObject(newResponse)); // "localhost:8081/api/v1/match/new"
                                                                                                                                                 //string answer = await client.GetStringAsync(url + "/new?map=" + _mapName);
                if (!String.IsNullOrWhiteSpace(answer))
                {
                    // TODO: create object to serialze to
                    try
                    {
                        NewMatchResponse result = JsonConvert.DeserializeObject<NewMatchResponse>(answer);

                        if (result.map == _mapName && result.nsServerName == _serverName)
                        {
                            matchId = result.id;
                        }
                        else
                        {
                            throw new WrongAnswerException("GetMatchId info for wrong match");
                        }
                    }
                    catch (JsonException _ex)
                    {
                        Console.WriteLine(_ex.ToString());
                    }
                }
            }
            else
            {
#pragma warning disable CS0162 // Unreachable code detected - Will be use when debuging
                matchId = "01234567-89ab-cdef-0123-456789abcdef";
#pragma warning restore CS0162 // Unreachable code detected
            }
        }

        /// <summary>
        /// Ends the match and sends this info to the backend
        /// </summary>
        /// <returns></returns>
        public async Task EndMatch()
        {
            if (PipeReader.argUseHttp)
                await Output.PutJsonHttpClient(GloVars.ArgUrl + "/match/end/" + matchId, String.Empty);
        }
    }
}
