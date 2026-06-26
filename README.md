# API Gateway

[![CI](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/ci.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/ci.yml)
[![Docker](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/docker.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/docker.yml)
[![Deploy](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/deploy.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/deploy.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Cloud](https://img.shields.io/badge/spring%20cloud-2022.0.4-blue)

Spring Cloud Gateway. Single entry point with JWT auth, circuit breakers, and Eureka service discovery.

## Stack

- Java 17, Spring Boot 3.1.2, Spring Cloud Gateway
- Eureka Discovery Client
- Resilience4j (Circuit Breaker, Retry, TimeLimiter)
- JWT (jjwt)
- Maven, JaCoCo

## Routes

| Service | Path | Backend |
|---------|------|---------|
| pet-service | `/api/pets/**` | `lb://pet-service` |
| geo-service | `/api/locations/**` | `lb://geo-service` |
| match-service | `/api/matching/**` | `lb://match-service` |
| bff | `/api/**` | `lb://bff` |

## Auth

| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/login` | Get JWT token |

## Quick start

```bash
mvn clean install
mvn spring-boot:run
```

## Tests

```bash
mvn test
mvn clean verify
```

## Deploy

GitHub Actions pushes to Docker Hub, SSM pulls and restarts on EC2. Orchestrates the full stack deployment.
