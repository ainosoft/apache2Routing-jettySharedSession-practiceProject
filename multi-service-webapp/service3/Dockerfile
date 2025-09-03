# Stage 1: Build the app
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# Expose the port your Spring Boot app listens on internally
EXPOSE 8080
# Command to run your Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
