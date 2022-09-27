# DataEndpoint

The DataEndpoint is a backend written in java to collect the incoming game data and process it for persistence storage and aggregation.
It can perform the necessary computation to generate the heatmaps and allows clients to hook up to it and receive the live game data via a Websocket.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Requirements

- JDK: 17 or newer
- Maven: 3.8.3 or newer
- PostgreSQL: 12 or newer
- ActiveMQ Artemis: 2.20.0
- Elasticsearch: 7.15.5 or newer

**Or**

- JDK: 17 or newer
- Docker

### Services

It is **highly** advised to use docker to manage all the required services used for the DataEndpoint.

All connection parameters are stored in the local. env or container.env file.

#### Docker

Start the services:
```shell
$ docker-compose -f ./install/docker/services/docker-compose.yml up
```
On the first startup you need to upload the elastic config.

```shell
$ ./install/elk/upload-elastic.bat
```
 

#### Local 

- TODO

### Build

#### Docker

Build the docker image:
```shell
$ docker-compose -f ./install/docker/base/docker-compose.yml build
```

Start the docker image:
```shell
$ docker-compose -f ./install/docker/base/docker-compose.yml up
```

#### Local

**Note:** This project requires the [NeoUtil](https://github.com/Neoministein/NeoUtil) dependency
in order to build the project locally.

**Install dependency**
```bash
$ cd ..

$ git clone https://github.com/Neoministein/NeoUtil.git

$ cd NeoUtil 

$ mvn clean install
```

**Full project build**
```bash
$ mvn clean package
```


**Run project integration tests**
```bash
$ mvn clean package -Pintegration
```

**Run project**

Replace parameters with the desired ones.
```bash
$ set javax.persistence.jdbc.url #jdbc:postgresql://localhost:5432/R2-TS
$ set javax.persistence.jdbc.user #DBACC
$ set javax.persistence.jdbc.password #DBACC

$ set org.elastic.node.0 #http://localhost:9200
$ set org.elastic.enabled true

$ set org.apache.activemq.url #tcp://127.0.0.1:61616
$ cd target

$ java -jar r2-telemetry-system-endpoint.jar
```

## Functionality

### Authentication

Most of the REST endpoints are secured and require authentication.

Required HTTP Header:
```
Authorization: Bearer <user-token>
```

#### Super user

On first startup you can generate a Super-User `GET /api/v1/user/init`.