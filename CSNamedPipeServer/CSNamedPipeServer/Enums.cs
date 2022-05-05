namespace CSNamedPipeServer
{
    public enum EventType
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

        DebugMessage = 99,
    }

    public enum LogMode
    {
        None = 0,       // No logs
        Event = 1,      // Event Logs
        Most = 2,       // Event+Pipe Logs
        All = 3,        // Event+Pipe+Send Logs
    }
}
