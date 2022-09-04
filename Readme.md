# Interstellar R2 Telemetry System

The R2 Telemetry System records player driven events inside a Titanfall 2 multiplayer match.

It is an extension of the [R2 Northstar Client](https://github.com/R2Northstar/Northstar) and currently uses a forked version of it.

## Development

The R2 Telemetry System consists of different components:

1. [Forked NorthstarLauncher](https://github.com/Aragami-delp/NorthstarLauncherWebSocket/tree/namedPipeClient) (Named pipe client and modified Squirrel VM)
1. [Interstellar.Telemetry](./Interstellar.Telemetry/Readme.md) (Telemetry data aggregation with Squirrel inside the game engine)
1. [C#NamedPipeServer](./CSNamedPipeServer/Readme.md) (Named Pipe Server in C# and middle man between Titanfall and the backend)
1. [DataEndpoint](./DataEndpoint/Readme.md) (Receives data from client forms the necessary computation and stores the data in java)
1. [DataFrontend](./DataFrontend) (The visualization built in react js)

