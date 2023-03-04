export DB_HOST=localhost DB_PORT=5432 DB_NAME=test-auth-db DB_USER=root DB_PASSWORD=secret
docker compose -f "src/main/resources/docker/docker-compose-test.yml" down
docker volume rm --force VOLUME test-auth-db
docker compose -f "src/main/resources/docker/docker-compose-test.yml" up -d --build
./mvnw test
