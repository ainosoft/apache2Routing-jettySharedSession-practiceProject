
FROM maven:3.8.8-eclipse-temurin-8-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests
FROM openjdk:8-jre-alpine

WORKDIR /app

COPY --from=build /app/target/jetty-hazelcast-session-1.0-SNAPSHOT.jar app.jar
COPY --from=build /app/target/lib/ lib/

EXPOSE 8080

CMD ["java", "-cp", "app.jar:lib/*", "hazelcastsession.JettyHazelcastServer"]
