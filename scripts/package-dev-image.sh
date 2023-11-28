docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME auth-db-dev
docker image rm authentication-server-dev-app:latest
docker image prune --force

./mvnw clean package -DskipTests

docker compose -f "docker-compose.yml" up -d --build
