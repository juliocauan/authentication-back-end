bash scripts/stop-dev.sh

mvn clean package -DskipTests

docker compose -f "docker-compose-dev.yml" up -d --build
