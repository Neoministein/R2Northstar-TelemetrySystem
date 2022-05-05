
# 1st stage, build the app
FROM maven:3.6-jdk-11 as build

#Install git
RUN apt-get install git
#RUN apt-get install openssh

WORKDIR /dependency/neoutil

## Download NeoUtil dependencies
RUN git clone https://github.com/Neoministein/NeoUtil.git /dependency/neoutil
RUN mvn clean install -Dmaven.test.skip



# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
WORKDIR /dataEndpoint

ADD pom.xml .
RUN mvn package -Dmaven.test.skip

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests
RUN echo "done!"

# 2nd stage, build the runtime image
FROM openjdk:11-jre-slim
WORKDIR /dataEndpoint

# Copy the binary built in the 1st stage
COPY --from=build /dataEndpoint/target/mapsystem-data-endpoint.jar ./
COPY --from=build /dataEndpoint/target/libs ./libs

CMD ["java", "-jar", "mapsystem-data-endpoint.jar"]

EXPOSE 8080
