bash src/test/package.sh
docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME auth-db-dev
docker image prune --force
docker compose -f "docker-compose.yml" up -d --build