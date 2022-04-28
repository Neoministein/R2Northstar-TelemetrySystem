namespace CSNamedPipeServer
{
    public struct Vector
    {
        public int x;
        public int y;
        public int z;

        public Vector(int _x, int _y, int _z)
        {
            x = _x; y = _y; z = _z;
        }

        public Vector(int[] _floats)
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
        public int timePassed;
    }
}
