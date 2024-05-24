docker compose -f "docker-compose-prod.yml" down
docker volume rm --force VOLUME authentication
docker image rm authentication-server:latest
docker image prune --force

mvn clean package -DskipTests

docker compose -f "docker-compose-prod.yml" up -d --build
