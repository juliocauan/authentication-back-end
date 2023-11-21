docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME auth-db-test

docker compose -f "docker-compose-test.yml" up -d --build
./mvnw jacoco:prepare-agent clean package jacoco:report

docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME auth-db-test

echo ''