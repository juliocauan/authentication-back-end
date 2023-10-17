docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME test-auth-db
docker compose -f "docker-compose-test.yml" up -d --build
./mvnw jacoco:prepare-agent clean package jacoco:report
if [ $? -eq 0 ]
then
    firefox target/site/jacoco/index.html &
fi
docker compose -f "docker-compose-test.yml" down
docker volume rm --force VOLUME test-auth-db
