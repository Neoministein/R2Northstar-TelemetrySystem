using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    public class Player
    {
        //public string id; // Not sure if needed since its in the dic
        public int team;
        public bool isWallRunning = false;
        public string health = "100";
        public string primary = "none";
        public string secondary = "none";
        public string weapon3 = "none";
        public bool isTitan = false;
        public bool isShooting = false;
        public bool isGrounded = true;
        public bool isHanging = true;
        public bool isCrouching = false;
        public bool isAlive = false;
        public string titanClass = "none";

        public Player(/*string _id, */int _team)
        {
            //id = _id;  
            team = _team;
        }
    }
}
