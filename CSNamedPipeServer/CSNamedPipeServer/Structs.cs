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

    public class DynamicInfos
    {
        public Player[] players;
        public string matchId;
        public string map;
        public Events events = new Events();
        public TimeSpan timePassed;
    }
}
