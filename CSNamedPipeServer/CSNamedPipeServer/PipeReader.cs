using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.IO.Pipes;
using System.Text;
using System.Collections;
using System;

namespace CSNamedPipeServer
{
    public class PipeInstance
    {
        // Http post erstellen
        // Http put schließen
        private NamedPipeServerStream m_namedPipeServer;
        private IMessageProcessor m_processor;

        public PipeInstance(NamedPipeServerStream _pipe, IMessageProcessor _processor)
        {
            m_namedPipeServer = _pipe;
            m_processor = _processor;
        }

        public void OnCloseApplication()
        {
            m_processor.HandleClose();
            m_namedPipeServer.Close();
        }

        /// <summary>
        /// Main Loop that executes the named pipe as well as handling http and json
        /// </summary>
        public async Task<int> RunPipe(CancellationToken _token = default)
        {
            byte[] readBuffer = new byte[102400];
            try
            {
                await m_namedPipeServer.WaitForConnectionAsync(_token);
                while
                    (m_namedPipeServer
                     .IsConnected) // TODO: Start new session when match ends or disconnect //!m_closed && m_server.IsConnected
                {
                    Memory<byte> memBuf = new Memory<byte>(readBuffer);
                    var bytesRead = await m_namedPipeServer.ReadAsync(memBuf, _token);

                    if (0 >= bytesRead)
                    {
                        break;
                    }

                    // TODO: Multithreading/Coroutine
                    try
                    {
                        if (ProcessInputFromPipe(Encoding.UTF8.GetString(readBuffer.AsSpan()).AsSpan()))
                        {
                            break;
                        }

                        readBuffer.Initialize();
                    }
                    catch (Exception _ex)
                    {
                        Console.WriteLine("RunPipe() process input exception: " + _ex.ToString());
                    }

                    readBuffer = new byte[102400];
                }
                m_processor.HandleClose();

                m_namedPipeServer.Close();
            }
            catch (Exception _ex)
            {
                Console.WriteLine("RunPipe() complete exception: " + _ex.ToString());
            }

            return 0;
        }

        public bool ProcessInputFromPipe(ReadOnlySpan<char> input)
        {
            if (GloVars.ArgLogMode >= LogMode.Most)
            {
                Console.WriteLine("NamedPipe read: " + input.ToString());
            }

            int index = input.IndexOf('\0');

            ReadOnlySpan<char> actualInput = index >= 0 ? input[..index] : input;

            if (actualInput == "Close")
            {
                return true;
            }

            string message = actualInput.ToString();

            if (GloVars.ArgLogMode >= LogMode.Most)
            {
                Console.WriteLine("NamedPipe result: " + String.Join("   ", message));
            }

            try
            {
                m_processor.Process(message);
            }
            catch (Exception _ex)
            {
                Console.WriteLine("RunPipe() command exception: " + _ex);
            }

            return false;
        }
    }

    public class PipeReader
    {
        // Http post erstellen
        // Http put schließen
        private bool closeApp = false;
        private NamedPipeServerStream m_generalPipe;
        private const int BUFFER_SIZE = 512; // Same as northstar.dll
        private const int TCHAR_SIZE = 2; // Same as northstar.dll
        private bool m_closed = false;

        public const bool argUseHttp = true;
        public const LogMode argLogMode = LogMode.All;

        public List<PipeInstance> m_runningGamePipes = new List<PipeInstance>();

        public void OnCloseApplication()
        {
            foreach (PipeInstance instance in m_runningGamePipes)
            {
                instance.OnCloseApplication();
            }
            closeApp = true;
            m_generalPipe.Dispose();
            //m_generalPipe = null;

            using (NamedPipeClientStream npcs = new NamedPipeClientStream("GameDataPipe"))
            {
                npcs.Connect(1000);
            }
        }

        /// <summary>
        /// Main Loop that executes the named pipe as well as handling http and json
        /// </summary>
        public int MainLoop()
        {
            m_generalPipe = OpenNewPipe("GameDataPipe", PipeDirection.Out);
            try
            {
                m_generalPipe.WaitForConnection();
            }
            catch (Exception ex)
            {
                if (ex is IOException)
                {
                    Console.WriteLine("General pipe closed");
                }
                else
                {
                    Console.Write(ex.ToString());
                }
                return 1;
            }
            if (!closeApp)
            {
                Console.WriteLine("General Pipe Connection: " + (m_generalPipe.IsConnected ? "connected" : "failed"));
                if (!m_generalPipe.IsConnected)
                {
                    Console.WriteLine("Error, closing server, no connection established!");
                    throw new ServerCreationException();
                }

                string localNewPipeName = GenerateId();
                string newPipeName = "\\\\.\\pipe\\" + localNewPipeName;
                Console.WriteLine("New Pipe id: " + localNewPipeName);
                byte[] generalWriteBuffer = Encoding.Unicode.GetBytes(newPipeName);
                Array.Resize(ref generalWriteBuffer, BUFFER_SIZE * TCHAR_SIZE);
                PipeInstance m_matchPipe = new PipeInstance(OpenNewPipe(localNewPipeName, PipeDirection.InOut), GloVars.GetProcessor());
                if (m_generalPipe.IsConnected) // TODO: Start new session when match ends or disconnect //!m_closed && m_server.IsConnected
                {
                    m_generalPipe.Write(generalWriteBuffer);
                    m_runningGamePipes.Add(m_matchPipe);
                    Task t1 = Task.Run(() => m_matchPipe.RunPipe()); // TODO: Async
                                                                     // Close to allow connection of new client for new match
                    m_generalPipe.Close();
                }
                //}
            }
            return 0;
        }

        #region PipeConnection
        public static NamedPipeServerStream OpenNewPipe(string _pipeName, PipeDirection _direction)
        {
            // TODO: Accept multiple pipes
            NamedPipeServerStream server = new NamedPipeServerStream(_pipeName, _direction, 1, PipeTransmissionMode.Byte, PipeOptions.Asynchronous);
            //server.WaitForConnection(); // TODO: Timeout
            //Console.WriteLine("Connection: " + (server.IsConnected ? "connected" : "failed"));
            //if (!server.IsConnected)
            //{
            //    Console.WriteLine("Error, closing server, no connection established!");
            //    throw new ServerCreationException();
            //}
            return server;
        }

        public static string GenerateId()
        {
            return Guid.NewGuid().ToString("N");
        }
        #endregion


    }
}
