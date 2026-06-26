# API Gateway

[![Docker](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/docker.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/docker.yml)
[![Deploy](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/deploy.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-api-gateway/actions/workflows/deploy.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Cloud](https://img.shields.io/badge/spring%20cloud-2022.0.4-blue)

Spring Cloud Gateway. Punto de entrada unico con ruteo, autenticacion JWT, circuit breakers y service discovery.

## Stack

- Java 17, Spring Boot 3.1.2, Spring Cloud Gateway 4.x
- Eureka Discovery Client
- Resilience4j (Circuit Breaker, Retry, TimeLimiter)
- JWT (jjwt 0.9.1)
- Maven, JaCoCo
- Docker multi-stage

## Patrones de Diseno

| Patron | Tipo | Donde |
|--------|------|-------|
| **API Gateway** | Arquitectonico | Punto de entrada unico con ruteo a microservicios |
| **Gateway Filter Factory** | Spring | `JwtAuthenticationGatewayFilterFactory` — filtro JWT personalizado |
| **Circuit Breaker** | Cloud / Resilience4j | 4 instancias (bff, pet, geo, match) con ventana deslizante de 10 |
| **Retry** | Cloud / Resilience4j | 3 intentos con backoff exponencial |
| **Time Limiter** | Cloud / Resilience4j | Timeout de 5s default, 10s por servicio |
| **Fallback** | Cloud | `FallbackController` — respuestas 503 estructuradas |
| **Front Controller** | Spring | `DispatcherServlet` + `@RestController` |

## Rutas

| Servicio | Path | Backend | Circuit Breaker | Retry |
|----------|------|---------|-----------------|-------|
| pet-service | `/api/pets/**` | `lb://pet-service` | ✅ | ✅ 3 intentos |
| geo-service | `/api/locations/**` | `lb://geo-service` | ✅ | ✅ 3 intentos |
| match-service | `/api/matching/**` | `lb://match-service` | ✅ | ✅ 3 intentos |
| bff | `/api/**` | `lb://bff` | ✅ | ✅ 3 intentos |

## Auth

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/auth/login` | Obtener token JWT |
| GET | `/fallback/{service}` | Respuesta de fallback (503) |

## Resilience4j Configuracion

**Circuit Breaker:**
- Ventana deslizante: 10 requests
- Umbral de fallo: 50%
- Tiempo de espera en OPEN: 5s

**Retry:**
- Max intentos: 3
- Wait duration: 2s
- Backoff exponencial: si (multiplicador 2)

**TimeLimiter:**
- Default: 5s
- Por servicio: 10s

## Pruebas

```bash
mvn clean test
mvn clean verify
```

21 tests en 4 archivos: `AuthControllerTest`, `FallbackControllerTest`, `JwtAuthenticationGatewayFilterFactoryTest`, `JwtUtilTest`.

## Docker

```bash
docker build -t d4mag3/api-gateway .
docker run -p 8080:8080 d4mag3/api-gateway
```

Imagen disponible en: `d4mag3/api-gateway:latest`

## Despliegue

El pipeline CI/CD via GitHub Actions:
1. Push a `main` → trigger `docker.yml`
2. Build imagen Docker
3. Push a Docker Hub (`d4mag3/api-gateway`)
4. AWS EC2 ejecuta `docker-compose pull && docker-compose up -d`

## Variables de Entorno

| Variable | Default | Descripcion |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Puerto del gateway |
| `JWT_SECRET` | (interno) | Clave secreta HMAC para JWT |
| `EUREKA_URL` | `http://eureka-server:8761/eureka/` | URL de Eureka |
