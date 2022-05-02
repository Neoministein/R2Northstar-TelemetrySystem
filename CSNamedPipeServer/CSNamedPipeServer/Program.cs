

namespace CSNamedPipeServer
{
    public static class Program
    {
        static async Task Main(string[] args)
        {
            PipeReader pipeReader = new PipeReader();
            pipeReader.MainLoop();
        }
    }
}