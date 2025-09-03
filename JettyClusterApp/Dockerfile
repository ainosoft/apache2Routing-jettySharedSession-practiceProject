FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copy the pre-built jar from local target directory
COPY target/jetty-cluster-session-1.0.0.jar ./app.jar

# Download MySQL Connector/J
RUN wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -O mysql-connector-j-8.0.33.jar

EXPOSE 8080

CMD ["java", "-cp", ".:mysql-connector-j-8.0.33.jar", "-jar", "app.jar"]
