# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
RUN apk --no-cache add curl
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Setting up environment and port
EXPOSE 8082

# Run the application with proper memory and JVM settings
ENTRYPOINT ["java", "-jar", "app.jar"]