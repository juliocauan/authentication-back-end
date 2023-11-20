# REST API for user Authentication and Authorization

![youshallnotpass](https://github.com/juliocauan/authentication-server/assets/84354526/e4d27e22-8a5f-4d74-aacc-b95119852c10)

***
## 📖  Description

This is the design of a Rest API for user authentication and authorization. It limits user access to only what has been specifically assigned to them. <br/>
This API was developed as a personal challenge for learning different areas of development, which are:
 - Cybersecurity
 - Web development
 - CI/CD
 - Database Administrator
 - Security and Data Retention Policies
 - Token Management
 - Registration and Audit
 - Integration with Identity Systems

<br/>

***
## 🛠️ Functionalities
**User Data Storage:**
   - Securely stores user data, including encrypted passwords and associated roles.

**User Authentication with JWT:**
   - Provides user authentication, generating JSON Web Tokens (JWT) upon successful login for secure access.

**"Forgot Password" Feature:**
   - Implements a secure "Forgot Password" functionality for users to reset their passwords.

**Admin Role Management:**
   - Enables administrators to update user roles for effective access control.

To see all endpoints and functionalities, visit [Authentication API OpenAPI(Swagger) Documentation](https://github.com/juliocauan/authentication-open-api)

<br/>

***
## 📡 Used Technologies 
<div align="center"> 
  <img align="left" alt="Spring" title="Spring" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg">
  <img align="left" alt="OpenAPI (Swagger)" title="OpenAPI (Swagger)" height="30" width="30" src="https://avatars.githubusercontent.com/u/37325267?s=200&v=4">
  <img align="left" alt="Postgresql" title="Postgresql" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original.svg">
  <img align="left" alt="Docker" title="Docker" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original.svg">
  <img align="left" alt="GitHub Actions" title="GitHub Actions" height="30" width="30" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/github/github-original.svg">
</div>
<br/><br/>

***
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
   
   - Scroll down and at the bottom right of this page, you will see this:
   ![tutorial1](https://github.com/juliocauan/authentication-server/assets/84354526/882ba442-ad23-4432-a6ca-c7cfd2cbca5c)

   - Click on "Use browser instead" and the configuration should look like this:
   ![tutorial2](https://github.com/juliocauan/authentication-server/assets/84354526/a90f5a2a-502e-4058-b6f5-92bbc66dddec)

   - Now use the provided information to interact with the API.

5. **Using Adminer:**

      To check the database(Docker Compose must be running), access [http://localhost:9000](http://localhost:9000) and fill the following fields like that:
    - System: PostgreSQL
    - Server: postgres
    - Username: root
    - Password: secret
    - Database: auth-db-dev
      <br/><br/>
      Login and change Schema from **public** to **auth**

***
### Additional Usage

- To package the project and run tests (port 5434 must be available):

  ```bash
  docker compose -f "docker-compose-test.yml" up -d --build
  ./mvnw clean package
  ```

<br/>

***
## 🔮 Future implementations

1. Extra Security Policies
   - Strong Passwords
   - Account block after failed login attemps
   - Email verification upon user registration

2. Multifactor Authentication

3. Authentication via Google

<br/>

***
## 🔎 Project Status

![Status Badge](https://img.shields.io/badge/status-development-green)

<br/>
