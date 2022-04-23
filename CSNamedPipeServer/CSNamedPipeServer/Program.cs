using Newtonsoft.Json;
using System;
using System.IO.Pipes;
using System.Text;

namespace CSNamedPipeServer
{
    public static class Program
    {
#pragma warning disable CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider declaring as nullable.
        private static NamedPipeServerStream m_server;
        private const int BUFFER_SIZE = 512;
        private const int TCHAR_SIZE = 2;
        private static Match match;
#pragma warning restore CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider declaring as nullable.

        static void Main(string[] args)
        {
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
            // Create match just in case
            match = new Match("none");
            while (!m_closed)
            {
                m_server.Read(buff, 0, BUFFER_SIZE * TCHAR_SIZE);
                string readString = Encoding.Unicode.GetString(buff);
                int index = readString.IndexOf('\0');
                if (index >= 0)
                    readString = readString.Substring(0, index);
                //if (!String.IsNullOrEmpty(tmp))
                if (readString == "Close")
                    break;

                string[] curLog = readString.Split('|');
                // Remove leading empty string
                if (curLog[0] == "")
                    curLog = curLog.Skip(1).ToArray();
                ProcessCommand(curLog);

                //Console.WriteLine(readString);
            }




            Console.ReadKey();
            //TODO: if prev string == current string discard it
        }

        static void ProcessCommand(string[] cmd)
        {
            LogType logType = (LogType)int.Parse(cmd[0]);
            switch (logType)
            {
                case LogType.DynamicInfos: // 0
                    // |0|PlayerCount|PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
                    //TODO: DynamicInfos
                    break;
                case LogType.WaitingForPlayers: // 1
                    // |1|MapName
                    match = new Match(cmd[1]);
                    break;
                case LogType.GameFinished: // 2
                    // |2|
                    break;
                case LogType.PlayerConnect: // 3
                    // |3|PlayerID|TeamID
                    match.players.Add(cmd[1], new Player(int.Parse(cmd[2])));
                    break;
                case LogType.PlayerDisconnect: // 4
                    // |4|PlayerID
                    match.players.Remove(cmd[1]);
                    break;
                case LogType.PlayerKilled: // 5
                    // |5|AttackerID|VictimID|Weapon
                    match.players[cmd[2]].isAlive = false;
                    break;
                case LogType.PlayerRespawned: // 6
                    match.players[cmd[1]].isAlive = true;
                    // |6|PlayerID
                    break;
                case LogType.PilotBecomesTitan: // 7
                    // |7|PlayerID|TitanClass
                    match.players[cmd[1]].isTitan = true;
                    match.players[cmd[1]].titanClass = cmd[2];
                    break;
                case LogType.TitanBecomesPilot: // 8
                    // |8|PlayerID
                    match.players[cmd[1]].isTitan = false;
                    break;
                case LogType.PlayerGetsNewPilotLoadout: // 9
                    // |9|PlayerID|Primary|Secondary|Weapon3
                    match.players[cmd[1]].primary = cmd[2];
                    match.players[cmd[1]].secondary = cmd[3];
                    match.players[cmd[1]].weapon3 = cmd[4];
                    break;
                case LogType.PlayerWallrun: // 10
                    // |10|PlayerID|isWallRunning
                    match.players[cmd[1]].isWallRunning = cmd[2] == "true";
                    break;
                case LogType.Shoot: // 11
                    // |11|PlayerID|isShooting
                    match.players[cmd[1]].isShooting = cmd[2] == "true";
                    break;
                case LogType.PlayerJump: // 12
                    // |12|PlayerID
                    //TODO: Jump
                    break;
                case LogType.PlayerDoubleJump: // 13
                    // |13|PlayerID
                    //TODO: DoubleJump
                    break;
                case LogType.PlayerGround: // 14
                    // |14|PlayerID|isInAir
                    match.players[cmd[1]].isGrounded = !(cmd[2] == "true");
                    break;
                case LogType.PlayerMantle: // 15
                    // |15|PlayerID
                    //TODO: Mantle
                    break;
                case LogType.PlayerWallHang: // 16
                    // |16|PlayerID|isHanging
                    match.players[cmd[1]].isHanging = cmd[2] == "true";
                    break;
                case LogType.PlayerCrouch: // 17
                    // |17|PlayerID|isCrouching
                    match.players[cmd[1]].isCrouching = cmd[2] == "true";
                    break;
            }
            if (logType == LogType.DynamicInfos)
                SendJsonDynamicInfos(cmd);
        }

        static void SendJsonDynamicInfos(string[] _cmd)
        {
            // |0|PlayerCount|PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
            DynamicInfos infos = new DynamicInfos(match.players[_cmd[2]].isWallRunning, new Vector(GetVectorData(_cmd[4])), match.players[_cmd[2]].health, _cmd[2], match.players[_cmd[2]].team, new Vector(GetVectorData(_cmd[3])), new Vector(GetVectorData(_cmd[5])), match.players[_cmd[2]].weapon3, match.players[_cmd[2]].isTitan, match.players[_cmd[2]].isShooting, match.players[_cmd[2]].primary);
            string json = JsonConvert.SerializeObject(infos, Formatting.Indented);
            Console.WriteLine(json);
        }

        static float[] GetVectorData(string _vector)
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
        public Dictionary<string, Player> players = new Dictionary<string, Player>();

        public Match(string _mapName)
        {
            mapName = _mapName;
        }
    }
}