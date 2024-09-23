bash scripts/stop-prod.sh

mvn clean package -DskipTests

docker compose -f "docker-compose-prod.yml" up -d --build
