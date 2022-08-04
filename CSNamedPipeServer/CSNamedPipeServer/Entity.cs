namespace CSNamedPipeServer;

public class Entity
{
             /// <summary>
            /// Unique entity id
            /// </summary>
            public string entityId = "";
            /// <summary>
            /// Team Id
            /// </summary>
            public byte team = 0;
            /// <summary>
            /// Health in percent
            /// </summary>
            public byte health = 0; // 8bit int
            /// <summary>
            /// Rotation (reduced to flat degree values)
            /// </summary>
            public Vector<int> rotation = new Vector<int>();
            /// <summary>
            /// Position (reduced to flat degree values)
            /// </summary>
            public Vector<int> position = new Vector<int>();
            /// <summary>
            /// Rotation (reduced to flat degree values)
            /// </summary>
            public Vector<int> velocity = new Vector<int>();
}