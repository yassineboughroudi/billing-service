# ----------------------------
# Stage 1: Build the JAR
# ----------------------------
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Install Maven (only if necessary)
RUN apk add --no-cache maven

# Copy Maven wrapper and configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies first for better caching
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# ----------------------------
# Stage 2: Create the runtime image
# ----------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Expose the service's port
EXPOSE 8080

# Copy the built .jar from the build stage
COPY --from=build /app/target/billing-service-0.0.1-SNAPSHOT.jar app.jar

# Ensure the JAR file has execution permissions
RUN chmod +x /app/app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
