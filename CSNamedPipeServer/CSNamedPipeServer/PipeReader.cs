using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.IO.Pipes;
using System.Text;
using System.Collections;

namespace CSNamedPipeServer
{
    internal class PipeReader
    {
        // Http post erstellen
        // Http put schließen
        private NamedPipeServerStream m_namedPipeServer;
        private const int BUFFER_SIZE = 512; // Same as northstar.dll
        private const int TCHAR_SIZE = 2; // Same as northstar.dll
        private Match m_currentMatch = new Match("empty0", "empty1", "empty2", false, true); // Needed, because its assigned outside of the constructor
        private DynamicInfos m_currentInfo = new DynamicInfos();
        private DateTime m_startTime = DateTime.Now;
        private bool m_closed = false;

        public const string argUrl = "http://localhost:8090/api/v1";

        public const bool argUseHttp = true;
        public const LogMode argLogMode = LogMode.Event;

        /// <summary>
        /// Main Loop that executes the named pipe as well as handling http and json
        /// </summary>
        public void MainLoop()
        {
            // TODO: Accept multiple pipes
            m_namedPipeServer = new NamedPipeServerStream("GameDataPipe", PipeDirection.In, 1, PipeTransmissionMode.Byte);
            m_namedPipeServer.WaitForConnection(); // TODO: Timeout
            Console.WriteLine("Connection: " + (m_namedPipeServer.IsConnected ? "connected" : "failed"));
            if (!m_namedPipeServer.IsConnected)
            {
                Console.WriteLine("Error, closing server, no connection established!");
                return;
            }

            byte[] readBuffer = new byte[BUFFER_SIZE * TCHAR_SIZE];



            while (m_namedPipeServer.IsConnected && 0 < m_namedPipeServer.Read(readBuffer, 0, BUFFER_SIZE * TCHAR_SIZE)) // TODO: Start new session when match ends or disconnect //!m_closed && m_server.IsConnected
            {
                // TODO: Multithreading/Coroutine
                
                string readString = Encoding.Unicode.GetString(readBuffer);
                if (argLogMode >= LogMode.Most)
                    Console.WriteLine("NamedPipe read: " + readString);
                int index = readString.IndexOf('\0');
                if (index >= 0)
                    readString = readString.Substring(0, index);
                if (readString == "Close")
                    break;

                string[] curLog = readString.Split('|');
                // Remove leading empty string
                if (curLog[0] == "")
                    curLog = curLog.Skip(1).ToArray();
                if (argLogMode >= LogMode.Most)
                    Console.WriteLine("NamedPipe result: " + String.Join("   ", curLog));
                try
                {
                    ProcessCommand(curLog);
                }
                catch (Exception _ex)
                {
                    Console.WriteLine(_ex);
                }
            }
            if (m_currentMatch.isRunning)
            {
                Console.WriteLine("Pipe connection lost. Ending current match");
                EndMatch();
            }
            m_namedPipeServer.Dispose();
            //Console.WriteLine("Exit: 0");
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
                        // |1|MapName|Gamemode|Servername
                        if (m_currentMatch.isRunning)
                        {
                            if (argLogMode >= LogMode.Event)
                                Console.WriteLine("Event: WaitingForPlayers: Ending previous match with id: " + m_currentMatch.matchId);
                            EndMatch();
                        }
                        if (argLogMode >= LogMode.Event)
                            Console.Write("Event: WaitingForPlayers: gamemode: " + cmd[2]+ ", mapName: " + cmd[1]);
                        m_currentMatch = new Match(cmd[1], cmd[2], cmd[3]);
                        m_startTime = DateTime.Now;
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine(", matchId: " + m_currentMatch.matchId);
                        break;
                    case EventType.GameFinished: // 2
                        // |2|
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: GameFinished");
                        EndMatch();
                        break;
                    case EventType.PlayerConnect: // 3
                        // |3|PlayerID|TeamID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: PlayerConnect: playerId: " + cmd[1] + ", teamId: " + cmd[2]);
                        m_currentMatch.players.Add(cmd[1], new Player(cmd[1], byte.Parse(cmd[2])));
                        m_currentInfo.events.eventPlayerConnect.Add(new Event_PlayerConnect(cmd[1], byte.Parse(cmd[2])));
                        break;
                    case EventType.PlayerDisconnect: // 4
                        // |4|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: PlayerDisconnect: playerId: " + cmd[1]);
                        m_currentMatch.players.Remove(cmd[1]);
                        m_currentInfo.events.eventPlayerDisconnect.Add(new Event_PlayerDisconnect(cmd[1]));
                        break;
                    case EventType.PlayerKilled: // 5
                        // |5|AttackerID|VictimID|Weapon
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerKilled: attackerId: " + cmd[1] + ", victimId: " + cmd[2] + ", weapon: " + cmd[3]);
                        m_currentMatch.players[cmd[2]].isAlive = false;
                        m_currentInfo.events.eventPlayerKilled.Add(new Event_PlayerKilled(cmd[1], cmd[2], cmd[3]));
                        break;
                    case EventType.PlayerRespawned: // 6
                        // |6|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerRespawned: playerId: " + cmd[1]);
                        m_currentMatch.players[cmd[1]].isAlive = true;
                        m_currentInfo.events.eventPlayerRespawned.Add(new Event_PlayerRespawned(cmd[1]));
                        break;
                    case EventType.PilotBecomesTitan: // 7
                        // |7|PlayerID|TitanClass
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: pilotBecomesTitan: playerId: " + cmd[1] + ", titanClass: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isTitan = true;
                        m_currentMatch.players[cmd[1]].titanClass = cmd[2];
                        m_currentInfo.events.eventPilotBecomesTitan.Add(new Event_PilotBecomesTitan(cmd[1], cmd[2]));
                        break;
                    case EventType.TitanBecomesPilot: // 8
                        // |8|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: titanBecomesPilot: playerId: " + cmd[1]);
                        m_currentMatch.players[cmd[1]].isTitan = false;
                        m_currentInfo.events.eventTitanBecomesPilot.Add(new Event_TitanBecomesPilot(cmd[1]));
                        break;
                    case EventType.PlayerGetsNewPilotLoadout: // 9
                        // |9|PlayerID|Primary|Secondary|Weapon3|Special
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerGetsNewPilotLoadout: playerId: " + cmd[1] + ", primary: " + cmd[2] + ", secondary: " + cmd[3] + ", weapon3: " + cmd[4] + ", special: " + cmd[5]);
                        m_currentMatch.players[cmd[1]].primary = cmd[2];
                        m_currentMatch.players[cmd[1]].secondary = cmd[3];
                        m_currentMatch.players[cmd[1]].weapon3 = cmd[4];
                        m_currentMatch.players[cmd[1]].special = cmd[5];
                        m_currentInfo.events.eventPlayerGetsNewPilotLoadout.Add(new Event_PlayerGetsNewPilotLoadout(cmd[1], cmd[2], cmd[3], cmd[4], cmd[5]));
                        break;
                    case EventType.PlayerWallrun: // 10
                        // |10|PlayerID|isWallRunning
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerWallrun: playerId: " + cmd[1] + ", isWallRunning: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isWallRunning = cmd[2] == "true";
                        break;
                    case EventType.Shoot: // 11
                        // |11|PlayerID|isShooting
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: shoot: playerId: " + cmd[1] + ", isShooting: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isShooting = cmd[2] == "true";
                        break;
                    case EventType.PlayerJump: // 12
                        // |12|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerJump: playerId: " + cmd[1]);
                        m_currentInfo.events.eventPlayerJump.Add(new Event_PlayerJump(cmd[1]));
                        break;
                    case EventType.PlayerDoubleJump: // 13
                        // |13|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerDoubleJump: playerId: " + cmd[1]);
                        m_currentInfo.events.eventPlayerDoubleJump.Add(new Event_PlayerDoubleJump(cmd[1]));
                        break;
                    case EventType.PlayerGround: // 14
                        // |14|PlayerID|isInAir
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerGround: playerId: " + cmd[1] + ", isInAir: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isGrounded = !(cmd[2] == "true");
                        break;
                    case EventType.PlayerMantle: // 15
                        // |15|PlayerID
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerMantle: playerId: " + cmd[1]);
                        m_currentInfo.events.eventPlayerMantle.Add(new Event_PlayerMantle(cmd[1]));
                        break;
                    case EventType.PlayerWallHang: // 16
                        // |16|PlayerID|isHanging
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerWallHang: playerId: " + cmd[1] + ", isHanging: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isHanging = cmd[2] == "true";
                        break;
                    case EventType.PlayerCrouch: // 17
                        // |17|PlayerID|isCrouching
                        if (argLogMode >= LogMode.Event)
                            Console.WriteLine("Event: playerCrouch: playerId: " + cmd[1] + ", isCrouching: " + cmd[2]);
                        m_currentMatch.players[cmd[1]].isCrouching = cmd[2] == "true";
                        break;
                    case EventType.DebugMessage: // 99
                        // |99|DebugText
                        Console.WriteLine("---- DebugText ----");
                        break;
                    default:
                        Console.WriteLine("Unknown command: " + cmd[0]);
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
            if (argUseHttp)
            {
                Task dontAwaite = Output.PutJsonHttpClient(argUrl + "/matchstate", json); // Exceptions here will be lost since there is not await
            }
            if (argLogMode >= LogMode.All)
                Console.WriteLine("Send: " + JValue.Parse(json).ToString(Formatting.Indented));
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
            {
                result[i] = int.Parse(strVec[i].Split('.').First());
            }
            return result;
        }
    }
}
