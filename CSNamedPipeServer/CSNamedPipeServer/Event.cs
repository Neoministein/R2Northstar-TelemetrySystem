using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    public class Events
    {
        public List<Event_PlayerConnect> playerConnect = new List<Event_PlayerConnect>(0);
        public List<Event_PlayerDisconnect> playerDisconnect = new List<Event_PlayerDisconnect>(0);
        public List<Event_PlayerKilled> playerKilled = new List<Event_PlayerKilled>(0);
        public List<Event_PlayerRespawned> playerRespawned = new List<Event_PlayerRespawned>(0);
        public List<Event_PilotBecomesTitan> pilotBecomesTitan = new List<Event_PilotBecomesTitan>(0);
        public List<Event_TitanBecomesPilot> titanBecomesPilot = new List<Event_TitanBecomesPilot>(0);
        public List<Event_PlayerGetsNewPilotLoadout> playerGetsNewPilotLoadout = new List<Event_PlayerGetsNewPilotLoadout>(0);
        public List<Event_PlayerJump> playerJump = new List<Event_PlayerJump>(0);
        public List<Event_PlayerDoubleJump> playerDoubleJump = new List<Event_PlayerDoubleJump>(0);
        public List<Event_PlayerMantle> playerMantle = new List<Event_PlayerMantle>(0);
    }

    public struct Event_PlayerConnect
    {
        public string entityId;
        public byte team;
        public Event_PlayerConnect(string _entityId, byte _team)
        {
            entityId = _entityId;
            team = _team;
        }
    }
    public struct Event_PlayerDisconnect
    {
        public string entityId;
        public Event_PlayerDisconnect(string _entityId)
        {
            entityId = _entityId;
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
        public string entityId;
        public Event_PlayerRespawned(string _entityId)
        {
            entityId = _entityId;
        }
    }
    public struct Event_PilotBecomesTitan
    {
        public string entityId;
        public string titanClass;
        public Event_PilotBecomesTitan(string _entityId, string _titanClass)
        {
            entityId = _entityId;
            titanClass = _titanClass;
        }
    }
    public struct Event_TitanBecomesPilot
    {
        public string entityId;
        public Event_TitanBecomesPilot(string _entityId)
        {
            entityId = _entityId;
        }
    }
    public struct Event_PlayerGetsNewPilotLoadout
    {
        public string entityId;
        public string primary;
        public string secondary;
        public string weapon3;
        public string special;
        public Event_PlayerGetsNewPilotLoadout(string _entityId, string _primary, string _secondary, string _weapon3, string _special)
        {
            entityId = _entityId;
            primary = _primary;
            secondary = _secondary;
            weapon3 = _weapon3;
            special = _special;
        }
    }
    public struct Event_PlayerJump
    {
        public string entityId;
        public Event_PlayerJump(string _entityId)
        {
            entityId = _entityId;
        }
    }
    public struct Event_PlayerDoubleJump
    {
        public string entityId;
        public Event_PlayerDoubleJump(string _entityId)
        {
            entityId = _entityId;
        }
    }
    public struct Event_PlayerMantle
    {
        public string entityId;
        public Event_PlayerMantle(string _entityId)
        {
            entityId = _entityId;
        }
    }
}
