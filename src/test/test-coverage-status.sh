source .env

# Exporta as variáveis de ambiente
export DB_HOST
export DB_PORT
export DB_NAME
export DB_USER
export DB_PASSWORD

docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME test-auth-db
docker compose -f "docker-compose.yml" up -d --build
./mvnw jacoco:prepare-agent clean package jacoco:report
if [ $? -eq 0 ]
then
    firefox target/site/jacoco/index.html &
fi
docker compose -f "docker-compose.yml" down
docker volume rm --force VOLUME test-auth-db
