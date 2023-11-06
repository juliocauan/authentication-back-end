bash src/test/package.sh
docker compose -f "docker-compose.yml" down
docker image rm --force IMAGE authentication-back-end-dev-app:latest
docker volume rm --force VOLUME auth-db-dev
docker compose -f "docker-compose.yml" up -d --build