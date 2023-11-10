# Base image for Java application
FROM eclipse-temurin:17-jre-alpine

# Expose the port Java application listens on
EXPOSE 8000

# Set the working directory
WORKDIR /auth

# Copy Java application JAR file into the container
COPY ./target/authentication-*.jar /authentication.jar

# Default environment variables for opensource production
ENV SPRING_PROFILES_ACTIVE=prod

# Command to run Java application
CMD ["java", "-jar", "/authentication.jar"]
