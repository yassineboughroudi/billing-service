# Use an OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/billing-service-0.0.1-SNAPSHOT.jar billing-service.jar

# Expose the service's port
EXPOSE 8086

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "billing-service.jar"]
