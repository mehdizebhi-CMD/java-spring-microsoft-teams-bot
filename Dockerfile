# Use the official OpenJDK 17 image as a base image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/tbot-0.0.1.jar /app/tbot.jar

# Expose the port that your Spring Boot application
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app/tbot.jar"]
