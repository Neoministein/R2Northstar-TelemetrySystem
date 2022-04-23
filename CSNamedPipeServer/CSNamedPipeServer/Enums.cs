using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    public enum LogType
    {
        DynamicInfos = 0,
        WaitingForPlayers = 1,
        GameFinished = 2,
        PlayerConnect = 3,
        PlayerDisconnect = 4,
        PlayerKilled = 5,
        PlayerRespawned = 6,
        PilotBecomesTitan = 7,
        TitanBecomesPilot = 8,
        PlayerGetsNewPilotLoadout = 9,
        PlayerWallrun = 10,
        Shoot = 11,
        PlayerJump = 12,
        PlayerDoubleJump = 13,
        PlayerGround = 14,
        PlayerMantle = 15,
        PlayerWallHang = 16,
        PlayerCrouch = 17,
    }
}
