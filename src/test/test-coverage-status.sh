export DB_HOST=localhost DB_PORT=5432 DB_NAME=test-auth-db DB_USER=root DB_PASSWORD=secret
docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME test-auth-db
docker compose -f "docker-compose.yml" up -d --build
./mvnw jacoco:prepare-agent clean package jacoco:report
if [ $? -eq 0 ]
then
    firefox target/site/jacoco/index.html &
fi
docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME test-auth-db
