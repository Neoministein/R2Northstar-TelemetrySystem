using System;
using System.Configuration;

namespace CSNamedPipeServer
{
    public static class Program
    {
        private static PipeReader m_pipeReader;
        private static bool m_keepRunning = true;
        static void Main(string[] args)
        {
            foreach (string arg in args)
            {
                if (arg == "-h" || arg == "-help")
                {
                    ShowHelp();
                    return;
                }
            }
            try
            {
                if (args.Length == 2)
                    GloVars.LoadSettings(args[0], args[1]);
                else if (args.Length == 0)
                    GloVars.LoadSettings();
                else
                    throw new InvalidArgumentsException("Invalid amount of arguments");
            }
            catch (InvalidArgumentsException _ex)
            {
                Console.WriteLine(_ex.Message);
                ShowHelp();
                return;
            }
            Console.WriteLine("Starting NamedPipeServer");
            m_pipeReader = new PipeReader();
            // TODO: ConsoleKeyPress for clean closior of tasks
            //Console.CancelKeyPress += delegate (object? _sender, ConsoleCancelEventArgs _e) // TODO: On windows close
            //{
            //    m_keepRunning = false;
            //    m_pipeReader.OnCloseApplication();
            //};
            Output.Init();
            while (m_keepRunning)
                m_pipeReader.MainLoop();
        }

        private static void ShowHelp()
        {
            Console.WriteLine("Syntax: filename.exe [userKey apiUrl]");
            Console.WriteLine("userKey: authentification key for the db server");
            Console.WriteLine("apiUrl: url of the db server");
            Console.WriteLine("Example if not started before or want to change settings: ThisServer.exe 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789A http://localhost:8090/api/v1");
            Console.WriteLine("Example if started before: ThisServer.exe");
        }
    }
}