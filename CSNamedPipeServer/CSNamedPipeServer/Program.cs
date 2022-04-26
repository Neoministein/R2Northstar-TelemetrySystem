

namespace CSNamedPipeServer
{
    public static class Program
    {
        static void Main(string[] args)
        {
            PipeReader pipeReader = new PipeReader();
            pipeReader.MainLoop();
        }
    }
}