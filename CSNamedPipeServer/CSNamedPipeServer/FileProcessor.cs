using System.Text;

namespace CSNamedPipeServer;

public class FileProcessor : IMessageProcessor
{
    private FileStream _fileStream;
    
    public FileProcessor()
    {
        Directory.CreateDirectory(Directory.GetCurrentDirectory() + "/match");
        _fileStream = File.Create(Directory.GetCurrentDirectory() + "/match/" + DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss") + ".txt");
    }
    public void Process(string _message)
    {
        byte[] messageBytes = new UTF8Encoding(true).GetBytes(_message + Environment.NewLine);
        _fileStream.Write(messageBytes, 0, messageBytes.Length);
    }

    public void HandleClose()
    {
        _fileStream.Close();
        _fileStream = File.Create(Directory.GetCurrentDirectory() + "/match/" + DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss") + ".txt");
    }
}