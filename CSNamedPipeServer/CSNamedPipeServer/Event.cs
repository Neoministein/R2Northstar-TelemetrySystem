using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    public class Events
    {
        public List<Event_PlayerConnect> eventPlayerConnect = new List<Event_PlayerConnect>(0);
        public List<Event_PlayerDisconnect> eventPlayerDisconnect = new List<Event_PlayerDisconnect>(0);
        public List<Event_PlayerKilled> eventPlayerKilled = new List<Event_PlayerKilled>(0);
        public List<Event_PlayerRespawned> eventPlayerRespawned = new List<Event_PlayerRespawned>(0);
        public List<Event_PilotBecomesTitan> eventPilotBecomesTitan = new List<Event_PilotBecomesTitan>(0);
        public List<Event_TitanBecomesPilot> eventTitanBecomesPilot = new List<Event_TitanBecomesPilot>(0);
        public List<Event_PlayerGetsNewPilotLoadout> eventPlayerGetsNewPilotLoadout = new List<Event_PlayerGetsNewPilotLoadout>(0);
        public List<Event_PlayerJump> eventPlayerJump = new List<Event_PlayerJump>(0);
        public List<Event_PlayerDoubleJump> eventPlayerDoubleJump = new List<Event_PlayerDoubleJump>(0);
        public List<Event_PlayerMantle> eventPlayerMantle = new List<Event_PlayerMantle>(0);
    }

    public struct Event_PlayerConnect
    {
        public string playerId;
        public byte team;
        public Event_PlayerConnect(string _playerId, byte _team)
        {
            playerId = _playerId;
            team = _team;
        }
    }
    public struct Event_PlayerDisconnect
    {
        public string playerId;
        public Event_PlayerDisconnect(string _playerId)
        {
            playerId = _playerId;
        }
    }
    public struct Event_PlayerKilled
    {
        public string attackerId;
        public string victimId;
        public string weapon;
        public Event_PlayerKilled(string _attackerId, string _victimId, string _weapon)
        {
            attackerId = _attackerId;
            victimId = _victimId;
            weapon = _weapon;
        }
    }
    public struct Event_PlayerRespawned
    {
        public string playerId;
        public Event_PlayerRespawned(string _playerId)
        {
            playerId = _playerId;
        }
    }
    public struct Event_PilotBecomesTitan
    {
        public string playerId;
        public string titanClass;
        public Event_PilotBecomesTitan(string _playerId, string _titanClass)
        {
            playerId = _playerId;
            titanClass = _titanClass;
        }
    }
    public struct Event_TitanBecomesPilot
    {
        public string playerId;
        public Event_TitanBecomesPilot(string _playerId)
        {
            playerId = _playerId;
        }
    }
    public struct Event_PlayerGetsNewPilotLoadout
    {
        public string playerId;
        public string primary;
        public string secondary;
        public string weapon3;
        public string special;
        public Event_PlayerGetsNewPilotLoadout(string _playerId, string _primary, string _secondary, string _weapon3, string _special)
        {
            playerId = _playerId;
            primary = _primary;
            secondary = _secondary;
            weapon3 = _weapon3;
            special = _special;
        }
    }
    public struct Event_PlayerJump
    {
        public string playerId;
        public Event_PlayerJump(string _playerId)
        {
            playerId = _playerId;
        }
    }
    public struct Event_PlayerDoubleJump
    {
        public string playerId;
        public Event_PlayerDoubleJump(string _playerId)
        {
            playerId = _playerId;
        }
    }
    public struct Event_PlayerMantle
    {
        public string playerId;
        public Event_PlayerMantle(string _playerId)
        {
            playerId = _playerId;
        }
    }
}
