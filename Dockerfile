FROM maven:3-jdk-11-slim AS build

WORKDIR /usr/local/src/server/
COPY . .
RUN mvn package -X

FROM debian:10-slim
ENV DEBIAN_FRONTEND=noninteractive

RUN mkdir -p /usr/share/man/man1 \
    && useradd -m serverd \
    && apt-get update -qm \
    && apt-get install -qy openjdk-11-jre-headless libpostgresql-jdbc-java wget

USER serverd
WORKDIR /home/serverd/

RUN mkdir lib \
    && wget https://jdbc.postgresql.org/download/postgresql-42.2.12.jar -O ./lib/postgresql.jar
COPY --chown=serverd:serverd --from=build /usr/local/src/server/target/server-1.0-SNAPSHOT.jar ./server.jar

ARG PORT=9876
EXPOSE ${PORT}

CMD ["java", "-cp", "lib/postgresql.jar", "-jar", "server.jar"]
