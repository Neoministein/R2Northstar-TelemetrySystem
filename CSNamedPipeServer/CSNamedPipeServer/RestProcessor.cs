using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CSNamedPipeServer;

public class RestProcessor : IMessageProcessor
{
    private Match m_currentMatch = new Match("empty0", "empty1", "empty2", "0",false, true); // Needed, because its assigned outside of the constructor
    private DynamicInfos m_currentInfo = new DynamicInfos();
    private DateTime m_startTime;
    private bool m_closed = false;
    public const bool argUseHttp = true;
    
    public void Process(string _message)
    {
        string[] cmd = _message.Split('|');
        
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
                    // |1|MapName|Gamemode|Servername|MaxPlayers
                    if (m_currentMatch.isRunning)
                    {
                        if (GloVars.ArgLogMode >= LogMode.Event)
                            Console.WriteLine("Event: WaitingForPlayers: Ending previous match with id: " + m_currentMatch.matchId);
                        EndMatch();
                    }
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.Write("Event: WaitingForPlayers: gamemode: " + cmd[2] + ", mapName: " + cmd[1]);
                    m_currentMatch = new Match(cmd[1], cmd[2], cmd[3], cmd[4]);
                    m_startTime = DateTime.Now;
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine(", matchId: " + m_currentMatch.matchId);
                    break;
                case EventType.GameFinished: // 2
                    // |2
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: GameFinished: matchId: " + m_currentMatch.matchId);
                    if (m_currentMatch.isRunning)
                    {
                        EndMatch();    
                    }
                    break;
                case EventType.PlayerConnect: // 3
                    // |3|PlayerID|TeamID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: PlayerConnect: playerId: " + cmd[1] + ", teamId: " + cmd[2]);
                    m_currentMatch.players.Add(cmd[1], new Player(cmd[1], byte.Parse(cmd[2])));
                    m_currentInfo.events.playerConnect.Add(new Event_PlayerConnect(cmd[1], byte.Parse(cmd[2])));
                    break;
                case EventType.PlayerDisconnect: // 4
                    // |4|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: PlayerDisconnect: playerId: " + cmd[1]);
                    m_currentMatch.players.Remove(cmd[1]);
                    m_currentInfo.events.playerDisconnect.Add(new Event_PlayerDisconnect(cmd[1]));
                    break;
                case EventType.EntityKilled: // 5
                    // |5|AttackerID|VictimID|Weapon
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: entityKilled: attackerId: " + cmd[1] + ", victimId: " + cmd[2] + ", damageType: " + cmd[3]);

                    if (m_currentMatch.players.ContainsKey(cmd[4]))
                    {
                        m_currentMatch.players[cmd[4]].isAlive = false;
                        m_currentMatch.players[cmd[4]].isTitan = false;
                    }
                    m_currentInfo.events.entityKilled.Add(new Event_EntityKilled(cmd[1], cmd[2], cmd[3], cmd[4], cmd[5]));
                    break;
                case EventType.PlayerRespawned: // 6
                    // |6|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerRespawned: playerId: " + cmd[1]);
                    m_currentMatch.players[cmd[1]].isAlive = true;
                    m_currentInfo.events.playerRespawned.Add(new Event_PlayerRespawned(cmd[1]));
                    break;
                case EventType.PilotBecomesTitan: // 7
                    // |7|PlayerID|TitanClass
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: pilotBecomesTitan: playerId: " + cmd[1] + ", titanClass: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isTitan = true;
                    m_currentMatch.players[cmd[1]].titanClass = cmd[2];
                    m_currentInfo.events.pilotBecomesTitan.Add(new Event_PilotBecomesTitan(cmd[1], cmd[2]));
                    break;
                case EventType.TitanBecomesPilot: // 8
                    // |8|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: titanBecomesPilot: playerId: " + cmd[1]);
                    m_currentMatch.players[cmd[1]].isTitan = false;
                    m_currentInfo.events.titanBecomesPilot.Add(new Event_TitanBecomesPilot(cmd[1]));
                    break;
                case EventType.PlayerGetsNewPilotLoadout: // 9
                    // |9|PlayerID|Primary|Secondary|Weapon3|Special
                    if (GloVars.ArgLogMode >= LogMode.Event) 
                        Console.WriteLine("Event: playerGetsNewPilotLoadout: playerId: " + cmd[1] + ", primary: " + cmd[2] + ", secondary: " + cmd[3] + ", weapon3: " + cmd[4] + ", special: " + cmd[5]);
                    m_currentMatch.players[cmd[1]].equipment.primary = cmd[2];
                    m_currentMatch.players[cmd[1]].equipment.secondary = cmd[3];
                    m_currentMatch.players[cmd[1]].equipment.weapon3 = cmd[4];
                    m_currentMatch.players[cmd[1]].equipment.special = cmd[5];
                    m_currentInfo.events.playerGetsNewPilotLoadout.Add(new Event_PlayerGetsNewPilotLoadout(cmd[1], cmd[2], cmd[3], cmd[4], cmd[5]));
                    break;
                case EventType.PlayerWallrun: // 10
                    // |10|PlayerID|isWallRunning
                    if (GloVars.ArgLogMode >= LogMode.Event) 
                        Console.WriteLine("Event: playerWallrun: playerId: " + cmd[1] + ", isWallRunning: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isWallRunning = cmd[2] == "true";
                    break;
                case EventType.Shoot: // 11
                    // |11|PlayerID|isShooting
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: shoot: playerId: " + cmd[1] + ", isShooting: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isShooting = cmd[2] == "true";
                    break;
                case EventType.PlayerJump: // 12
                    // |12|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerJump: playerId: " + cmd[1]);
                    m_currentInfo.events.playerJump.Add(new Event_PlayerJump(cmd[1]));
                    break;
                case EventType.PlayerDoubleJump: // 13
                    // |13|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerDoubleJump: playerId: " + cmd[1]);
                    m_currentInfo.events.playerDoubleJump.Add(new Event_PlayerDoubleJump(cmd[1]));
                    break;
                case EventType.PlayerGround: // 14
                    // |14|PlayerID|isInAir
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerGround: playerId: " + cmd[1] + ", isInAir: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isGrounded = !(cmd[2] == "true");
                    break;
                case EventType.PlayerMantle: // 15
                    // |15|PlayerID
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerMantle: playerId: " + cmd[1]);
                    m_currentInfo.events.playerMantle.Add(new Event_PlayerMantle(cmd[1]));
                    break;
                case EventType.PlayerWallHang: // 16
                    // |16|PlayerID|isHanging
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerWallHang: playerId: " + cmd[1] + ", isHanging: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isHanging = cmd[2] == "true"; 
                    break;
                case EventType.PlayerCrouch: // 17
                    // |17|PlayerID|isCrouching
                    if (GloVars.ArgLogMode >= LogMode.Event)
                        Console.WriteLine("Event: playerCrouch: playerId: " + cmd[1] + ", isCrouching: " + cmd[2]);
                    m_currentMatch.players[cmd[1]].isCrouching = cmd[2] == "true";
                    break;
                case EventType.DynamicBasicNpcInfo: // 30
                        
                    ProcessDynamicBasicNpcInfo(int.Parse(cmd[1]), cmd.Skip(2).ToArray());
                    break;
                case EventType.DynamicWeaponNpcInfo: // 31
                    ProcessDynamicWeaponNpcInfo(int.Parse(cmd[1]), cmd.Skip(2).ToArray());
                    break;
                case EventType.DynamicTitanNpcInfo: // 32
                    ProcessDynamicTitanNpcInfo(int.Parse(cmd[1]), cmd.Skip(2).ToArray());
                    break;
                case EventType.DebugMessage: // 99
                    // |99|DebugText
                    Console.WriteLine("---- DebugText ----");
                    break;
                case EventType.NpcLeeched:
                    m_currentInfo.events.npcLeeched.Add(new Event_NpcLeeched(cmd[1], cmd[2]));
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

    public void HandleClose()
    {
        if (m_currentMatch.isRunning)
        {
            EndMatch();
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
        m_currentMatch.isRunning = false;
        m_closed = true;
        Task t1 = Task.Run(() => m_currentMatch.EndMatch().Wait());
    }
    
    public void ProcessDynamicBasicNpcInfo(int _count, string[] _cmd)
    {
        // PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
        for (int i = 0; i < _count; i++)
        {
            Npc npc = new Npc();
            npc.npcClass = _cmd[0 + (i * 7)];
            npc.entityId = _cmd[1 + (i * 7)];
            npc.team = int.Parse(_cmd[2 + (i * 7)]);
            npc.position = new Vector<int>(GetVectorData(_cmd[3 + (i * 7)]));
            npc.rotation = new Vector<int>(GetVectorData(_cmd[4 + (i * 7)]));
            npc.velocity = new Vector<int>(GetVectorData(_cmd[5 + (i * 7)]));
            npc.health = (byte)Math.Clamp(double.Parse(_cmd[6 + (i * 7)]), 0, 100);
            m_currentInfo.npcs.Add(npc);
        }
    }
    
    public void ProcessDynamicWeaponNpcInfo(int _count, string[] _cmd)
    {
        // PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
        for (int i = 0; i < _count; i++)
        {
            NpcWithWeapon npc = new NpcWithWeapon();
            npc.npcClass = _cmd[0 + (i * 9)];
            npc.entityId = _cmd[1 + (i * 9)];
            npc.team = int.Parse(_cmd[2 + (i * 9)]);
            npc.position = new Vector<int>(GetVectorData(_cmd[3 + (i * 9)]));
            npc.rotation = new Vector<int>(GetVectorData(_cmd[4 + (i * 9)]));
            npc.velocity = new Vector<int>(GetVectorData(_cmd[5 + (i * 9)]));
            npc.health = (byte)Math.Clamp(double.Parse(_cmd[6 + (i * 9)]), 0, 100);
            npc.primary = _cmd[7 + (i * 9)];
            npc.secondary = _cmd[8 + (i * 9)];
            m_currentInfo.npcs.Add(npc);
        }
    }
    
    public void ProcessDynamicTitanNpcInfo(int _count, string[] _cmd)
    {
        // PlayerID|Position<x,y,z>|Rotation<x,y,z>|Velocity<x,y,z>|HealthInPercent
        for (int i = 0; i < _count; i++)
        {
            NpcTitan npc = new NpcTitan();
            npc.npcClass = _cmd[0 + (i * 8)];
            npc.entityId = _cmd[1 + (i * 8)];
            npc.team = int.Parse(_cmd[2 + (i * 8)]);
            npc.position = new Vector<int>(GetVectorData(_cmd[3 + (i * 8)]));
            npc.rotation = new Vector<int>(GetVectorData(_cmd[4 + (i * 8)]));
            npc.velocity = new Vector<int>(GetVectorData(_cmd[5 + (i * 8)]));
            npc.health = (byte)Math.Clamp(double.Parse(_cmd[6 + (i * 8)]), 0, 100);
            npc.titanClass = _cmd[7 + (i * 8)];
            m_currentInfo.npcs.Add(npc);
        }
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
            Task dontAwaite = Output.PutJsonHttpClient(GloVars.ArgUrl + "/matchstate", json); // Exceptions here will be lost since there is not await
        }
        if (GloVars.ArgLogMode >= LogMode.All)
            Console.WriteLine("Send: " + JValue.Parse(json).ToString(Formatting.Indented));
        // nTODO: Add to queue - might not be neccesary anymore since its done in tasks which s result arent waited for
        m_currentInfo = new DynamicInfos();
    }
}