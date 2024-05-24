docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME auth-test

docker compose -f "docker-compose-test.yml" up -d --build
mvn jacoco:prepare-agent clean package jacoco:report

docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME auth-test
