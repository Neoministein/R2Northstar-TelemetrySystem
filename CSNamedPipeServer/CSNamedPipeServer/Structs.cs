namespace CSNamedPipeServer
{
    /// <summary>
    /// Holds x, y, z as int
    /// </summary>
    public struct Vector<T>
    {
        public T x;
        public T y;
        public T z;

        /// <summary>
        /// Stores three values 
        /// </summary>
        /// <param name="_x">First value</param>
        /// <param name="_y">Second value</param>
        /// <param name="_z">Thrid value</param>
        public Vector(T _x, T _y, T _z)
        {
            x = _x; y = _y; z = _z;
        }

        /// <summary>
        /// Stores three values 
        /// </summary>
        /// <param name="_values">Array of three values, more will be ignored</param>
        public Vector(T[] _values)
        {
            x = _values[0];
            y = _values[1];
            z = _values[2];
        }
    }

    /// <summary>
    /// Store data about one update instance
    /// </summary>
    public class DynamicInfos
    {
        /// <summary>
        /// Array of all players currently on the server
        /// </summary>
        public Player[] players;
        /// <summary>
        /// Unique id of the match (given by the backend during construction)
        /// </summary>
        public string matchId;
        /// <summary>
        /// Map name
        /// </summary>
        public string map;
        /// <summary>
        /// All events that happend since the last instance of this
        /// </summary>
        public Events events = new Events();
        /// <summary>
        /// Time passed since start of the game in milliseconds
        /// </summary>
        public int timePassed;
    }
}
