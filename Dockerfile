# Use an appropriate base image with Java installed
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/DataGeneration-1.0-SNAPSHOT.jar /app/DataGeneration-1.0-SNAPSHOT.jar

# Expose the port on which your application runs (change it if needed)
EXPOSE 8080

# Set the command to run your Spring Boot application
CMD ["java", "-jar", "DataGeneration-1.0-SNAPSHOT.jar"]
