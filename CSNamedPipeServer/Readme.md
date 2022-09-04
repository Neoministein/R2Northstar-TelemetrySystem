# C# Named Pipe Server

The C# Named Pipe Server connects the Northstar-Client with the DataBackend.
It can manage any number of local Northstar-Clients and provides each one of them its own managed named pipe instance. 
The server parses the incoming data to json and sends it to the DataBackend's REST API.

## Requirements

 - .Net 6.0

## Functionality

This chapter describes the functionality for the C# Named Pipe Server.

### Startup

The C# Named Pipe Server requires two startup arguments.
```
CSNamedPipeServer.exe %USER_ACCESS_TOKEN% %DATA_BACKEND_URL% 
```

### Pipe Connection

A master pipe `GameDataPipe` is opened and waits for an initial connection. 

On connection, it opens a new named pipe sends the client the name of the pipe. 
Further communication is then done via that pipe until the match ends.

> A pipe lives as long as a match