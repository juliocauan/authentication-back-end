FROM eclipse-temurin:17-jre-alpine

EXPOSE 8443

WORKDIR /auth

COPY ./target/authentication-*.jar /authentication.jar

CMD ["java", "-jar", "/authentication.jar"]
