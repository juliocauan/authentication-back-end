FROM eclipse-temurin:17-jdk-alpine

EXPOSE 8000

WORKDIR /auth

ENV DB_HOST=postgres DB_PORT=5432
ENV DB_NAME=auth-db DB_USER=root DB_PASSWORD=secret

COPY ./*.jar /authentication.jar

CMD [ "java", "-jar", "/authentication.jar" ]
