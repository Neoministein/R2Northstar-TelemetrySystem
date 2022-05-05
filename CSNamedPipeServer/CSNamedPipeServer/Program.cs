using System;

namespace CSNamedPipeServer
{
    public static class Program
    {
        static async Task Main(string[] args)
        {
            do
            {
                Console.WriteLine("Starting NamedPipeServer");
                PipeReader pipeReader = new PipeReader();
                pipeReader.MainLoop();
                Console.WriteLine("Server will restart in 5 sec.\nPress any key to cancel...");
            }
            while (!Reader.KeyPressed(5000));
        }
    }

    // Based on: https://stackoverflow.com/questions/57615/how-to-add-a-timeout-to-console-readline
    class Reader
    {
        private static Thread inputThread;
        private static AutoResetEvent getInput, gotInput;
        private static ConsoleKeyInfo input;

        static Reader()
        {
            getInput = new AutoResetEvent(false);
            gotInput = new AutoResetEvent(false);
            inputThread = new Thread(reader);
            inputThread.IsBackground = true;
            inputThread.Start();
        }

        private static void reader()
        {
            while (true)
            {
                getInput.WaitOne();
                input = Console.ReadKey();
                gotInput.Set();
            }
        }

        // omit the parameter to read a line without a timeout
        public static bool KeyPressed(int timeOutMillisecs = Timeout.Infinite)
        {
            getInput.Set();
            bool success = gotInput.WaitOne(timeOutMillisecs);
            if (success)
                return true;
            else
                return false;
        }
    }
}