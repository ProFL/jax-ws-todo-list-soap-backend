FROM gradle:6.1-jdk11 AS build

WORKDIR /usr/local/src/server/
COPY . .
RUN gradle build \
    && gradle shadowJar

FROM adoptopenjdk:11-jre-hotspot
RUN useradd -m serverd
USER serverd
WORKDIR /home/serverd/

COPY --chown=serverd:serverd --from=build /usr/local/src/server/build/libs/server-1.0-SNAPSHOT-all.jar ./server.jar

ARG PORT=9876
EXPOSE ${PORT}

CMD ["java", "-jar", "server.jar"]
