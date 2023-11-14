./mvnw clean package -Dmaven.test.skip

docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME auth-db-dev
docker image prune --force

docker compose -f "docker-compose.yml" up -d --build