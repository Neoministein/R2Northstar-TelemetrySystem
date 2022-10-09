namespace CSNamedPipeServer;

public interface IMessageProcessor
{
    void Process(string _message);

    void HandleClose();
}