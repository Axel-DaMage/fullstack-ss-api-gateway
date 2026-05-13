# API Gateway

API Gateway para el proyecto **Sanos y Salvos**. Actúa como punto de entrada único para todos los microservicios, gestionando el enrutamiento, equilibrio de carga y mecanismos de resiliencia.

## Objetivo

El API Gateway centraliza las peticiones del cliente hacia los microservicios correspondientes, proporcionando una capa de seguridad, control y resiliencia. Implementa circuit breakers para manejar fallos en los servicios downstream y redirecciona a endpoints de fallback cuando los servicios no están disponibles.

## Arquitectura

### Patrón Gateway
El gateway actúa como puerta de enlace unificada:

```
Frontend → API Gateway → Pet Service / Geo Service / Match Service / BFF
```

### Componentes

- [FallbackController](src/main/java/com/sanosysalvos/apigateway/controller/FallbackController.java): Controlador de respuestas alternativas
- Configuration de Spring Cloud Gateway: Rutas y filtros
- Resilience4j: Circuit Breaker y Retry

### Rutas configuradas

| Servicio | URI | Path | Puerto |
|----------|-----|------|--------|
| pet-service | http://pet-service:3001 | /api/pets/** | 3001 |
| geo-service | http://geo-service:3002 | /api/locations/** | 3002 |
| match-service | http://match-service:3003 | /api/matching/** | 3003 |
| bff-service | http://bff:8081 | /api/** | 8081 |

## Endpoints de Fallback

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/fallback/pet-service` | Fallback para Pet Service |
| GET | `/fallback/geo-service` | Fallback para Geo Service |
| GET | `/fallback/match-service` | Fallback para Match Service |
| GET | `/fallback/bff` | Fallback para BFF |
| GET | `/fallback/health` | Estado del sistema de fallback |

## Características

### Circuit Breaker
Implementa Resilience4j con configuración personalizada para cada servicio:

- `slidingWindowSize`: 10
- `minimumNumberOfCalls`: 5
- `failureRateThreshold`: 50%
- `waitDurationInOpenState`: 10s

### Retry
Configuración de reintentos para tolerancia a fallos:

- `maxAttempts`: 3
- `waitDuration`: 1s
- `exponentialBackoffMultiplier`: 2

### CORS
Configuración global de CORS que permite:
- Origin: *
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: *

## Tecnologías

- Java 17
- Spring Boot 3
- Spring Cloud Gateway
- Resilience4j (Circuit Breaker, Retry)
- Maven

## Configuración

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: pet-service
          uri: http://pet-service:3001
          predicates:
            - Path=/api/pets/**
          filters:
            - CircuitBreaker=petServiceCircuitBreaker
```

## Instalación

```bash
mvn clean install
mvn spring-boot:run
```

## Pruebas

```bash
mvn test
```

## Monitoreo

El API Gateway expone endpoints de monitoreo:

- `/actuator/health`: Estado general del gateway
- `/actuator/info`: Información de la aplicación
- `/actuator/circuitbreakers`: Estado de los circuit breakers

## Notas

- El gateway implementa el patrón Facade proporcionando una interfaz unificada.
- Proporciona respuestas estructuradas en JSON cuando los servicios no están disponibles.
- El sistema de Circuit Breaker protege contra fallos en cascada.
- Los reintentos automáticos mejoran la resiliencia ante fallos transitorios.
- Configuración de CORS flexible para desarrollo y producción.

---

## Despliegue en AWS EC2

### Arquitectura

| Instancia | Servicios | Tipo |
|-----------|-----------|------|
| Backend (t3.medium) | pet-service, geo-service, match-service | Backend Core |
| Edge (t3.small) | api-gateway, bff, frontend | Edge & UI |
| RDS (db.t3.micro) | 3 bases de datos | Database |

### Pre-requisitos

1. **RDS**: Crear las siguientes bases de datos:
   - `pet_service`
   - `geo_service`
   - `match_service`

2. **Instancias EC2**: 2 instancias con User Data:
   - Backend: ejecutar `scripts/userdata-backend.sh`
   - Edge: ejecutar `scripts/userdata-edge.sh`

### Configuración de GitHub Secrets

En Settings > Secrets del repositorio:

| Secret | Descripción |
|--------|-------------|
| `EC2_EDGE_HOST` | IP pública instancia Edge |
| `EC2_USERNAME` | Usuario SSH (ubuntu) |
| `EC2_SSH_KEY` | Clave privada RSA |

### Puertos requeridos (Security Group Edge)

- 22 (SSH)
- 80 (frontend)
- 8080 (api-gateway)
- 8081 (bff)

### Despliegue automático

El deploy se ejecuta automáticamente al hacer push a `main`:
- Este repositorio desplieja a instancia **Edge** (api-gateway, bff, frontend)
- El repositorio **pet-service** desplieja a instancia **Backend**

### Verificación

```bash
# Ver servicios en Edge
curl http://EDGE_IP:8080/api/pets      # via gateway
curl http://EDGE_IP:8081/api/pets      # bff directo
curl http://EDGE_IP:80                 # frontend
```