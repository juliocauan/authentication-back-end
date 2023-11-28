FROM eclipse-temurin:17-jre-alpine

EXPOSE 8000

WORKDIR /auth

COPY ./target/authentication-*.jar /authentication.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "/authentication.jar"]
