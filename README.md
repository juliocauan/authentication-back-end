# Authentication and Authorization API - Server

![youshallnotpass](https://github.com/juliocauan/authentication-server/assets/84354526/e4d27e22-8a5f-4d74-aacc-b95119852c10)

## 📖  Description

This is a tutorial to run this API locally on DEV mode. <br/>
Obs: DEV mode will only mock emails, it won't send real ones. Production mode will be available shortly. <br/>

Check the [documentation](https://github.com/juliocauan/authentication-docs) to see all endpoints and functionalities.
<br/>

## 📡 Used Technologies 
<div align="center"> 
  <img align="left" alt="Spring" title="Spring" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg">
  <img align="left" alt="OpenAPI (Swagger)" title="OpenAPI (Swagger)" height="30" width="30" src="https://avatars.githubusercontent.com/u/37325267?s=200&v=4">
  <img align="left" alt="Postgresql" title="Postgresql" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original.svg">
  <img align="left" alt="Docker" title="Docker" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original.svg">
  <img align="left" alt="GitHub Actions" title="GitHub Actions" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/github/github-original.svg">
</div>
<br/><br/>

## 🚀 Getting Started
This project was developed in a Linux environment, using Ubuntu 22.04 and the technologies mentioned above. If you use another operating system, the initial project configuration may be slightly different. <br/>
These instructions will help you set up and run the project locally.

### Prerequisites

Make sure you have the following tools installed:

- [Java 17](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

Also make sure you have the following ports available:

- 8000 (Application)
- 9000 (Adminer)
- 5432 (PostgreSQL)

***
### Installation and Configuration

1. **Clone the repository:**

    ```bash
    git clone --recurse-submodules https://github.com/juliocauan/authentication-server.git
    cd authentication-server
    ```

2. **Package the Project:**

   - Run the following command to package the project without running tests:

    ```bash
    ./mvnw clean package -DskipTests
    ```

3. **Running the Project:**

   - Run the following command to start the API on DEV mode along with PostgreSQL database using Docker Compose:

     ```bash
     docker compose -f "docker-compose.yml" up -d --build
     ```

   - Wait until the container is up and running.
   - API will be running on port 8000 (DEV mode doesn't send real emails)

4. **Access the API:**

   - With the project running, access the API documentation:

     [Authentication API OpenAPI(Swagger) Documentation](https://app.swaggerhub.com/apis/juliocauan/authentication/1.1.x)

   - Scroll down the right tab of this page:

     ![swagger-tutorial](https://github.com/juliocauan/authentication-server/assets/84354526/4ee04ce0-cff0-4df2-8924-8bf997aafb30)
   
   - At the bottom right, you will see this:

     ![tutorial1](https://github.com/juliocauan/authentication-server/assets/84354526/882ba442-ad23-4432-a6ca-c7cfd2cbca5c)

   - Click on "Use browser instead" and the configuration should look like this:

     ![tutorial2](https://github.com/juliocauan/authentication-server/assets/84354526/a90f5a2a-502e-4058-b6f5-92bbc66dddec)

   - Now use the provided information to interact with the API.

5. **Using Adminer:**

      To check the database (Docker Compose must be running), access [http://localhost:9000](http://localhost:9000) and fill the following fields like that:
    - System: PostgreSQL
    - Server: postgres
    - Username: root
    - Password: secret
    - Database: auth-dev
    - Login and change Schema from **public** to **auth**

### Additional Usage

- To package the project and run tests (port 5434 must be available):

  ```bash
  docker compose -f "docker-compose-test.yml" up -d --build
  ./mvnw clean package
  ```

<br/>
