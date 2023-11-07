# Base image for Java application
FROM eclipse-temurin:17-jre-alpine

# Expose the port Java application listens on
EXPOSE 8000

# Set the working directory
WORKDIR /auth

# Copy Java application JAR file into the container
COPY ./**/authentication-*.jar /authentication.jar

# Default environment variables for opensource production
ENV SPRING_PROFILES_ACTIVE=prod

ENV DB_NAME=auth-db
ENV DB_PORT=5432
ENV DB_USER=sa
ENV DB_PASSWORD=sa

ENV APP_DOMAIN=http://localhost:4200

# Command to run Java application
CMD ["java", "-jar", "/authentication.jar"]
