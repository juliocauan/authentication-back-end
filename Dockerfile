# Use an appropriate base image for your Java application
FROM eclipse-temurin:17-jre-alpine

# Expose the port your Java application listens on
EXPOSE 8000

# Set the working directory
WORKDIR /auth

# Copy your Java application JAR file into the container
COPY ./**/authentication-*.jar /authentication.jar

# Define the command to run your Java application
ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "/authentication.jar"]
