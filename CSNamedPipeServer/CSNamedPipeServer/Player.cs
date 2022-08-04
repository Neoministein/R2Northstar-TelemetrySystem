namespace CSNamedPipeServer
{
    public class Player : Entity
    {
        /// <summary>
        /// Players equipment
        /// </summary>
        public Equipment equipment = new Equipment();
        /// <summary>
        /// Type of titan
        /// </summary>
        public string titanClass = "none";
        /// <summary>
        /// Currently wallrunning
        /// </summary>
        public bool isWallRunning = false;
        /// <summary>
        /// Currently piloting titan
        /// </summary>
        public bool isTitan = false;
        /// <summary>
        /// Currently Shooting
        /// </summary>
        public bool isShooting = false;
        /// <summary>
        /// Currently on the ground
        /// </summary>
        public bool isGrounded = true;
        /// <summary>
        /// Currently hanging on a ledge
        /// </summary>
        public bool isHanging = false;
        /// <summary>
        /// Currently crouching
        /// </summary>
        public bool isCrouching = false;
        /// <summary>
        /// Currently alive
        /// </summary>
        public bool isAlive = false;

        /// <summary>
        /// Creates a new player
        /// </summary>
        /// <param name="_id">Unique id</param>
        /// <param name="_team">Team id</param>
        public Player(string _id, byte _team)
        {
            entityId = _id;
            team = _team;
        }

        public class Equipment
        {
            /// <summary>
            /// Primary damageType
            /// </summary>
            public string primary = "none";
            /// <summary>
            /// Secondary damageType
            /// </summary>
            public string secondary = "none";
            /// <summary>
            /// Third damageType
            /// </summary>
            public string weapon3 = "none";
            /// <summary>
            /// Pilot ability
            /// </summary>
            public string special = "none";
        }
    }
}
