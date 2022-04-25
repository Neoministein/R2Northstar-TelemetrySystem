namespace CSNamedPipeServer
{
    public class Player
    {
        public byte team = 0;
        public byte health = 0; // 8bit int
        public Vector rotation = new Vector();
        public Vector position = new Vector();
        public Vector velocity = new Vector();
        public string playerId = "";
        public string primary = "none";
        public string secondary = "none";
        public string weapon3 = "none";
        public string special = "none";
        public string titanClass = "none";
        public bool isWallRunning = false;
        public bool isTitan = false;
        public bool isShooting = false;
        public bool isGrounded = true;
        public bool isHanging = true;
        public bool isCrouching = false;
        public bool isAlive = false;

        public Player(string _id, byte _team)
        {
            playerId = _id;
            team = _team;
        }
    }
}
