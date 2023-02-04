FROM ubuntu:latest

RUN apt-get update
RUN apt-get dist-upgrade -y
RUN apt-get autoclean -y
RUN apt-get autoremove -y
RUN apt-get check
RUN apt-get install openjdk-17-jdk -y

EXPOSE 8000

WORKDIR /auth

ENV DB_URL=jdbc:postgresql://localhost:5432/dev-auth-db PORT=8000
ENV DB_USER=root DB_PASSWORD=secret

COPY authentication authentication

# CMD [ "./main" ]