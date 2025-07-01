########################################################################################
# Single Stage: Build & Run with Amazon Corretto 17
########################################################################################

# Use Amazon Corretto 17 JDK as base image for build and runtime
FROM amazoncorretto:17

# Set working directory
WORKDIR /app

# Copy Gradle Wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy application source code
COPY src src

# Build the application
RUN ./gradlew bootJar

# Set environment variables (optional)
ENV JVM_OPTS=""

# Expose container port (Spring Boot default 8080)
EXPOSE 8080

# Run the application using prod profile
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar build/libs/*.jar --spring.profiles.active=prod"]
