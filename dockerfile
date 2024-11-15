# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and necessary files for dependency caching (optional)
COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle

# Copy the source code into the container
COPY . /app

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Run Gradle build to create the application JAR file, skipping tests
RUN ./gradlew clean build -x test

# Expose the port that the application will run on
EXPOSE 8080

# Set environment variable to activate the 'local' profile
#ENV SPRING_PROFILES_ACTIVE=local

# Run the Spring Boot application with the built JAR file
ENTRYPOINT ["java", "-jar", "build/libs/java-refactoring-test-0.0.1-SNAPSHOT.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]

#ENTRYPOINT ["java","-jar","/app.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]

