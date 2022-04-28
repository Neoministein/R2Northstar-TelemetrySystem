using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.IO.Pipes;
using System.Text;
using System.Collections;

namespace CSNamedPipeServer
{
    internal class PipeReader
    {
        private NamedPipeServerStream m_server = null;
        private const int BUFFER_SIZE = 512;
        private const int TCHAR_SIZE = 2;
        private Match m_currentMatch = new Match(""); // only testing
        private Queue<DynamicInfos> m_sendInfos = new Queue<DynamicInfos>();
        private DynamicInfos m_currentInfo = new DynamicInfos();

        public void MainLoop()
        {
            // TODO: Accept multiple pipes
            bool m_closed = false;
            m_server = new NamedPipeServerStream("GameDataPipe", PipeDirection.In, 1, PipeTransmissionMode.Byte);
            m_server.WaitForConnection();
            Console.WriteLine("Connection: " + m_server.IsConnected);
            if (!m_server.IsConnected)
            {
                Console.WriteLine("Error, closing server, no connection established!");
                return;
            }

            byte[] buff = new byte[BUFFER_SIZE * TCHAR_SIZE];



            while (!m_closed)
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

                ProcessCommand(curLog);
            }




            Console.ReadKey();
            //TODO: if prev string == current string discard it
        }

        public async void ProcessCommand(string[] cmd)
        {
            bool sendCurrentInfo = false;
            EventType logType = (EventType)int.Parse(cmd[0]);
            switch (logType)
            {
                case EventType.DynamicInfos: // 0
                    // |0|PlayerCount|PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
                    ProcessDynamicInfos(int.Parse(cmd[1]), cmd.Skip(2).ToArray());
                    sendCurrentInfo = true;
                    break;
                case EventType.WaitingForPlayers: // 1
                    // |1|MapName
                    // TODO: Get match key (http GET) and save it for a new match
                    m_currentMatch = new Match(cmd[1]);
                    break;
                case EventType.GameFinished: // 2
                    // |2|
                    // TODO: Send with other events or send by its own?
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

        public void ProcessDynamicInfos(int _count, string[] _cmd)
        {
            // PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
            for (int i = 0; i < _count; i++)
            {
                m_currentMatch.players[_cmd[(i * 5)]].position = new Vector(GetVectorData(_cmd[1 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].rotation = new Vector(GetVectorData(_cmd[2 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].velocity = new Vector(GetVectorData(_cmd[3 + (i * 5)]));
                m_currentMatch.players[_cmd[(i * 5)]].health = (byte)Math.Clamp(0, 100, double.Parse(_cmd[4 + (i * 5)])); // Not sure how accurate health is saved in squirrel
            }
        }

        public void SendJsonDynamicInfos()
        {
            m_currentInfo.players = m_currentMatch.players.Values.ToArray();
            m_currentInfo.matchId = m_currentMatch.matchId;
            m_currentInfo.map = m_currentMatch.mapName;
            string json = JsonConvert.SerializeObject(m_currentInfo);
            Console.WriteLine(json); // TODO: Send
            // Add to queue and send async
            m_currentInfo = new DynamicInfos();
        }

        public static float[] GetVectorData(string _vector)
        {
            _vector = _vector.Substring(1, _vector.Length-2);
            string[] strVec = _vector.Split(", ");
            float[] result = new float[strVec.Length];
            for (int i = 0; i < strVec.Length; i++)
                result[i] = float.Parse(strVec[i]);
            return result;
        }
    }

    public class Match
    {
        public string mapName;
        public string matchId;
        public Dictionary<string, Player> players = new Dictionary<string, Player>();

        /// <summary>
        /// Creates a new match
        /// </summary>
        /// <param name="_mapName">Name of map</param>
        /// <param name="_matchId">Id of map given by the backend</param>
        public Match(string _mapName)
        {
            mapName = _mapName;
            Task t1 = Task.Run(() => GetMatchId(_mapName));
        }

        static readonly HttpClient client = new HttpClient();

        public async Task GetMatchId(string _mapName)
        {
            string servername = "TestServerName";
            string url = "localhost:8080/api/v1/game";
            string answer = await client.GetStringAsync(url + "/new?map=" + _mapName);
            if (!String.IsNullOrWhiteSpace(answer))
            {
                // TODO: create object to serialze to
                NewMatchResponse result = JsonConvert.DeserializeObject<NewMatchResponse>(answer);
                foreach (NewMatchResponseData item in result.data.Where(data => data.nsServerName == servername))
                {
                    matchId = item.id;
                }
            }
        }
    }

    public class NewMatchResponse
    {
        public string apiVersion;
        public List<NewMatchResponseData> data;
        public string context;
        public string status;
    }

    public class NewMatchResponseData
    {
        public bool isRunning;
        public string nsServerName;
        public string id;
        public string map;
    }
}
