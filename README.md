# Autenticação e Autorização API - Servidor

![youshallnotpass](https://github.com/juliocauan/authentication-server/assets/84354526/e4d27e22-8a5f-4d74-aacc-b95119852c10)

## 📖  Descrição

Estas instruções ajudarão você a configurar e executar o projeto localmente.

Cheque também a [documentação](https://github.com/juliocauan/authentication-docs) para ver mais detalhes sobre API e suas funcionalidades.

## 📡 Tecnologias usadas 
<div align="center">
  <img align="left" alt="Spring" title="Spring" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg">
  <img align="left" alt="OpenAPI (Swagger)" title="OpenAPI (Swagger)" height="30" width="30" src="https://avatars.githubusercontent.com/u/37325267?s=200&v=4">
  <img align="left" alt="Postgresql" title="Postgresql" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original.svg">
  <img align="left" alt="Docker" title="Docker" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original.svg">
  <img align="left" alt="GitHub Actions" title="GitHub Actions" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/github/github-original.svg">
</div>
<br/>

## 🛠️ Pré-requisitos

- Certifique-se de ter as seguintes ferramentas instaladas:
  - [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - [Docker](https://www.docker.com/get-started)
  - [Docker Compose](https://docs.docker.com/compose/install/)

- Portas necessárias:
  - 8000 (API)
  - 8001 (Adminer)

## 🖥 Executando

- Clone o repositório:

    ```bash
    git clone https://github.com/juliocauan/authentication-server.git
    cd authentication-server
    ```

- Inicie a API junto ao PostgreSQL usando:

    ```bash
    docker compose -f "docker-compose-prod.yml" up -d --build
    ```

- Aguarde até que o contêiner esteja rodando.

## 🚀 Acessando a API

- Com o projeto em execução, acesse a [página Swagger(OpenAPI)](https://app.swaggerhub.com/apis/juliocauan/authentication/1.5.x-oas3):

- Role para baixo na guia direita desta página:

  ![swagger-tutorial](https://github.com/juliocauan/authentication-server/assets/84354526/4ee04ce0-cff0-4df2-8924-8bf997aafb30)

- No canto inferior direito, você verá isso:

  ![tutorial1](https://github.com/juliocauan/authentication-server/assets/84354526/882ba442-ad23-4432-a6ca-c7cfd2cbca5c)

- Clique em "**Use browser instead**" e a configuração deverá ficar assim:

  ![tutorial2](https://github.com/juliocauan/authentication-server/assets/84354526/a90f5a2a-502e-4058-b6f5-92bbc66dddec)

- Agora use as informações fornecidas para interagir com a API.

- PS: Para criar uma conta como ADMIN, a **adminKey** é **@Admin123**

## 💡 **Uso adicional: Adminer**

- Para verificar o banco de dados, acesse [localhost:8001](http://localhost:8001) e preencha os seguintes campos assim:
  - System: PostgreSQL
  - Server: db
  - Username: admin
  - Password: admin
  - Database: authentication

- Faça o login
- Mude o Schema de **public** para **auth**
