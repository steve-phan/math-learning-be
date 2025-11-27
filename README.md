# Math Learning API

AI-Powered Homework Checking Platform Backend. This service provides the core API for the Math Learning platform, handling user management, homework processing, and integration with AI services.

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.1
- **Database**: PostgreSQL
- **Caching**: Redis
- **Security**: Spring Security with JWT
- **Migrations**: Flyway
- **AI Integration**: OpenAI GPT-3/4, Anthropic Claude
- **Storage**: AWS S3 / MinIO

## Prerequisites

- Java 21 SDK
- Maven 3.9+
- Docker & Docker Compose (optional, for running dependencies)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/steve-phan/math-learning-be.git
cd math-learning-be
```

### 2. Environment Configuration

The application requires certain environment variables to be set. You can set these in your IDE or in a `.env` file (if using a dotenv plugin, though Spring Boot standard is `application.properties`/`application.yml`).

Key variables likely needed (check `application.yml` for specifics):
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `OPENAI_API_KEY`
- `ANTHROPIC_API_KEY`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

### 3. Running with Docker Compose (Recommended for Dev)

To start the database and Redis:

```bash
docker-compose up -d postgres redis
```

To run the entire stack including the application:

```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

### 4. Running Locally

Ensure PostgreSQL and Redis are running (you can use `docker-compose up -d postgres redis`).

```bash
mvn spring-boot:run
```

## Build

To build the project and run tests:

```bash
mvn clean package
```

To skip tests:

```bash
mvn clean package -DskipTests
```

## API Documentation

(Add link to Swagger/OpenAPI UI if available, usually at `/swagger-ui.html` or `/v3/api-docs`)
