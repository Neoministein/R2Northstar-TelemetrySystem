using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Net.Http.Json;
using System.Net.Http;
using System.IO.Pipes;
using System.Text;
using System.Collections;

namespace CSNamedPipeServer
{
    internal class PipeReader
    {
        // Http post erstellen
        // Http put schließen
        private NamedPipeServerStream m_server;
        private const int BUFFER_SIZE = 512;
        private const int TCHAR_SIZE = 2;
        private Match m_currentMatch = new Match("empty", "empty", true); // Needed, because its assigned outside of the constructor
        private Queue<DynamicInfos> m_sendInfos = new Queue<DynamicInfos>();
        private DynamicInfos m_currentInfo = new DynamicInfos();
        private DateTime m_startTime = DateTime.Now;
        private bool m_closed = false;

        public static string servername = "TestServerName";
        public static string url = "http://localhost:8081/api/v1";

        /// <summary>
        /// Main Loop that executes the named pipe as well as handling http and json
        /// </summary>
        public void MainLoop()
        {
            // TODO: Accept multiple pipes
            m_server = new NamedPipeServerStream("GameDataPipe", PipeDirection.In, 1, PipeTransmissionMode.Byte);
            m_server.WaitForConnection(); // TODO: Timeout
            Console.WriteLine("Connection: " + m_server.IsConnected);
            if (!m_server.IsConnected)
            {
                Console.WriteLine("Error, closing server, no connection established!");
                return;
            }

            byte[] buff = new byte[BUFFER_SIZE * TCHAR_SIZE];



            while (!m_closed && m_server.IsConnected) // TODO: Start new session when match ends or disconnect
            {
                // TODO: Multithreading/Coroutine
                m_server.Read(buff, 0, BUFFER_SIZE * TCHAR_SIZE);
                string readString = Encoding.Unicode.GetString(buff);
                int index = readString.IndexOf('\0');
                if (index >= 0)
                    readString = readString.Substring(0, index);
                if (readString == "Close")
                    break;

                string[] curLog = readString.Split('|');
                // Remove leading empty string
                if (curLog[0] == "")
                    curLog = curLog.Skip(1).ToArray();
                try
                {
                    ProcessCommand(curLog);
                }
                catch (Exception _ex)
                {
                    Console.WriteLine(_ex);
                }
            }




            //Console.ReadKey();
            //TODO: if prev string == current string discard it
        }

        /// <summary>
        /// Processes a given command depending of command type
        /// </summary>
        /// <param name="cmd">Array of commands recived by the named pipe</param>
        public void ProcessCommand(string[] cmd)
        {
            bool sendCurrentInfo = false;
            try
            {
                switch ((EventType)int.Parse(cmd[0]))
                {
                    case EventType.DynamicInfos: // 0
                        // |0|PlayerCount|PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
                        ProcessDynamicInfos(int.Parse(cmd[1]), cmd.Skip(2).ToArray());
                        sendCurrentInfo = true;
                        break;
                    case EventType.WaitingForPlayers: // 1
                        // |1|MapName|Gamemode
                        m_currentMatch = new Match(cmd[1], cmd[2], false);
                        m_startTime = DateTime.Now;
                        break;
                    case EventType.GameFinished: // 2
                        // |2|
                        EndMatch();
                        break;
                    case EventType.PlayerConnect: // 3
                        // |3|PlayerID|TeamID
                        m_currentMatch.players.Add(cmd[1], new Player(cmd[1], byte.Parse(cmd[2])));
                        m_currentInfo.events.eventPlayerConnect.Add(new Event_PlayerConnect(cmd[1], byte.Parse(cmd[2])));
                        break;
                    case EventType.PlayerDisconnect: // 4
                        // |4|PlayerID
                        m_currentMatch.players.Remove(cmd[1]);
                        m_currentInfo.events.eventPlayerDisconnect.Add(new Event_PlayerDisconnect(cmd[1]));
                        break;
                    case EventType.PlayerKilled: // 5
                        // |5|AttackerID|VictimID|Weapon
                        m_currentMatch.players[cmd[2]].isAlive = false;
                        m_currentInfo.events.eventPlayerKilled.Add(new Event_PlayerKilled(cmd[1], cmd[2], cmd[3]));
                        break;
                    case EventType.PlayerRespawned: // 6
                        // |6|PlayerID
                        m_currentMatch.players[cmd[1]].isAlive = true;
                        m_currentInfo.events.eventPlayerRespawned.Add(new Event_PlayerRespawned(cmd[1]));
                        break;
                    case EventType.PilotBecomesTitan: // 7
                        // |7|PlayerID|TitanClass
                        m_currentMatch.players[cmd[1]].isTitan = true;
                        m_currentMatch.players[cmd[1]].titanClass = cmd[2];
                        m_currentInfo.events.eventPilotBecomesTitan.Add(new Event_PilotBecomesTitan(cmd[1], cmd[2]));
                        break;
                    case EventType.TitanBecomesPilot: // 8
                        // |8|PlayerID
                        m_currentMatch.players[cmd[1]].isTitan = false;
                        m_currentInfo.events.eventTitanBecomesPilot.Add(new Event_TitanBecomesPilot(cmd[1]));
                        break;
                    case EventType.PlayerGetsNewPilotLoadout: // 9
                        // |9|PlayerID|Primary|Secondary|Weapon3|Special
                        m_currentMatch.players[cmd[1]].primary = cmd[2];
                        m_currentMatch.players[cmd[1]].secondary = cmd[3];
                        m_currentMatch.players[cmd[1]].weapon3 = cmd[4];
                        m_currentMatch.players[cmd[1]].special = cmd[5];
                        m_currentInfo.events.eventPlayerGetsNewPilotLoadout.Add(new Event_PlayerGetsNewPilotLoadout(cmd[1], cmd[2], cmd[3], cmd[4], cmd[5]));
                        break;
                    case EventType.PlayerWallrun: // 10
                        // |10|PlayerID|isWallRunning
                        m_currentMatch.players[cmd[1]].isWallRunning = cmd[2] == "true";
                        break;
                    case EventType.Shoot: // 11
                        // |11|PlayerID|isShooting
                        m_currentMatch.players[cmd[1]].isShooting = cmd[2] == "true";
                        break;
                    case EventType.PlayerJump: // 12
                        // |12|PlayerID
                        m_currentInfo.events.eventPlayerJump.Add(new Event_PlayerJump(cmd[1]));
                        break;
                    case EventType.PlayerDoubleJump: // 13
                        // |13|PlayerID
                        m_currentInfo.events.eventPlayerDoubleJump.Add(new Event_PlayerDoubleJump(cmd[1]));
                        break;
                    case EventType.PlayerGround: // 14
                        // |14|PlayerID|isInAir
                        m_currentMatch.players[cmd[1]].isGrounded = !(cmd[2] == "true");
                        break;
                    case EventType.PlayerMantle: // 15
                        // |15|PlayerID
                        m_currentInfo.events.eventPlayerMantle.Add(new Event_PlayerMantle(cmd[1]));
                        break;
                    case EventType.PlayerWallHang: // 16
                        // |16|PlayerID|isHanging
                        m_currentMatch.players[cmd[1]].isHanging = cmd[2] == "true";
                        break;
                    case EventType.PlayerCrouch: // 17
                        // |17|PlayerID|isCrouching
                        m_currentMatch.players[cmd[1]].isCrouching = cmd[2] == "true";
                        break;
                }
                if (sendCurrentInfo)
                {
                    SendJsonDynamicInfos();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }

        /// <summary>
        /// Processes the update info for all players
        /// </summary>
        /// <param name="_count">Amount of players</param>
        /// <param name="_cmd">Array of commands</param>
        public void ProcessDynamicInfos(int _count, string[] _cmd)
        {
            // PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
            for (int i = 0; i < _count; i++)
            {
                m_currentMatch.players[_cmd[(i * 5)]].position = new Vector<int>(GetVectorData(_cmd[1 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].rotation = new Vector<int>(GetVectorData(_cmd[2 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].velocity = new Vector<int>(GetVectorData(_cmd[3 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].health = (byte)Math.Clamp(double.Parse(_cmd[4 + (i * 5)]), 0, 100); // Not sure how accurate health is saved in squirrel
            }
        }

        /// <summary>
        /// Ends the match
        /// </summary>
        public void EndMatch()
        {
            m_closed = true;
            Task t1 = Task.Run(() => m_currentMatch.EndMatch().Wait());
        }

        /// <summary>
        /// Sends the current update info to the server - doesnt wait for send completion
        /// </summary>
        public void SendJsonDynamicInfos()
        {
            m_currentInfo.players = m_currentMatch.players.Values.ToArray();
            m_currentInfo.matchId = m_currentMatch.matchId;
            m_currentInfo.map = m_currentMatch.mapName;
            m_currentInfo.timePassed = (int)(DateTime.Now - m_startTime).TotalMilliseconds;
            string json = JsonConvert.SerializeObject(m_currentInfo);
            //Console.WriteLine(json);
            Task dontAwaite = UpDownData.PutJsonHttpClient(url + "/matchstate", json); // Exceptions here will be lost since there is not await
            // nTODO: Add to queue - might not be neccesary anymore since its done in tasks which s result arent waited for
            m_currentInfo = new DynamicInfos();
        }

        /// <summary>
        /// Gets the data of a vector in int given a string with exact matching format
        /// </summary>
        /// <param name="_vector">String with this format: "&lt;x, y, z&gt;"</param>
        /// <returns>Int array of three vector values</returns>
        public static int[] GetVectorData(string _vector)
        {
            _vector = _vector.Substring(1, _vector.Length-2);
            string[] strVec = _vector.Split(", ");
            int[] result = new int[strVec.Length];
            for (int i = 0; i < strVec.Length; i++)
                result[i] = (int)float.Parse(strVec[i]);
            return result;
        }
    }

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
        public string gamemode;
        /// <summary>
        /// Dictionary of all players currently on the server
        /// </summary>
        public Dictionary<string, Player> players = new Dictionary<string, Player>();

        /// <summary>
        /// Creates a new match
        /// </summary>
        /// <param name="_mapName">Name of map</param>
        /// <param name="_matchId">Id of map given by the backend</param>
        public Match(string _mapName, string _gamemode, bool dontAskServer = false)
        {
            mapName = _mapName;
            gamemode = _gamemode;
            if (!dontAskServer)
            {
                Task t1 = Task.Run(() => AssignMatchId(_mapName, _gamemode).Wait());
            }
        }

        /// <summary>
        /// Assins a match id
        /// </summary>
        /// <param name="_mapName">Name of the current map</param>
        /// <param name="_gamemode">Gamemode short term</param>
        /// <returns></returns>
        /// <exception cref="WrongAnswerException">Thrown when the data is for a different server</exception>
        public async Task AssignMatchId(string _mapName, string _gamemode)
        {
            NewMatchRequest newResponse = new NewMatchRequest() { map = _mapName, ns_server_name = PipeReader.servername, gamemode = _gamemode };
            string answer = await UpDownData.PostJsonHttpClient(PipeReader.url + "/match/new", JsonConvert.SerializeObject(newResponse)); // "localhost:8081/api/v1/match/new"
            //string answer = await client.GetStringAsync(url + "/new?map=" + _mapName);
            if (!String.IsNullOrWhiteSpace(answer))
            {
                // TODO: create object to serialze to
                try
                {
                    NewMatchResponse result = JsonConvert.DeserializeObject<NewMatchResponse>(answer);

                    if (result.map == _mapName && result.nsServerName == PipeReader.servername)
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

        /// <summary>
        /// Ends the match and sends this info to the backend
        /// </summary>
        /// <returns></returns>
        public async Task EndMatch()
        {
            await UpDownData.PutJsonHttpClient(PipeReader.url + "/match/end/" + matchId, String.Empty);
        }
    }

    public class NewMatchResponse
    {
        public bool isRunning;
        public string nsServerName;
        public string id;
        public string map;
    }

    public class NewMatchRequest
    {
        public string map;
        public string ns_server_name;
        public string gamemode;
    }

    /// <summary>
    /// Throw when getting data meant for a different server
    /// </summary>
    public class WrongAnswerException : Exception
    {
        public WrongAnswerException() { }

        public WrongAnswerException(string _message) : base(_message) { }

        public WrongAnswerException(string _message, Exception _inner) : base(_message, _inner) { }
    }
}
