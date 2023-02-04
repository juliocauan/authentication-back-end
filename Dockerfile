FROM ubuntu:latest

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
RUN apt-get autoclean -y
RUN apt-get autoremove -y
RUN apt-get check

EXPOSE 8000

WORKDIR /auth

ENV DB_HOST=postgres DB_PORT=5432
ENV DB_NAME=auth-db DB_USER=root DB_PASSWORD=secret

COPY ./*.jar authentication.jar

 CMD [ "java", "-jar", "/authentication.jar" ]
