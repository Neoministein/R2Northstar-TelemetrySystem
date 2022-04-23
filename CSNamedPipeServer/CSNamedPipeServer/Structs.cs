using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    public struct Vector
    {
        public float x;
        public float y;
        public float z;

        public Vector(float _x, float _y, float _z)
        {
            x = _x; y = _y; z = _z;
        }

        public Vector(float[] _floats)
        {
            x = _floats[0];
            y = _floats[1];
            z = _floats[2];
        }
    }

    public struct DynamicInfos
    {
        public bool isWallRunning;
        public Vector rotation;
        public string health;
        public string id;
        public int team;
        public Vector position;
        public Vector velocity;
        public string weapon3;
        public bool isTitan;
        public bool isShooting;
        public string primary;

        public DynamicInfos(bool _isWallRunning, Vector _rotation, string _health, string _id, int _team, Vector _position, Vector _velocity, string _weapon3, bool _isTitan, bool _isShooting, string _primary)
        {
            isWallRunning = _isWallRunning; rotation = _rotation; health = _health; id = _id; team = _team; position = _position; velocity = _velocity; weapon3 = _weapon3; isTitan = _isTitan; isShooting = _isShooting; primary = _primary;
        }
    }
}
