package com.sanosysalvos.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void petServiceFallbackReturnsServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/pet-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody()
            .jsonPath("$.error").isEqualTo(true)
            .jsonPath("$.service").isEqualTo("pet-service")
            .jsonPath("$.message").isEqualTo("Pet Service temporalmente no disponible")
            .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
            .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    void geoServiceFallbackReturnsServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/geo-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody()
            .jsonPath("$.error").isEqualTo(true)
            .jsonPath("$.service").isEqualTo("geo-service")
            .jsonPath("$.message").isEqualTo("Geo Service temporalmente no disponible")
            .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
            .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    void matchServiceFallbackReturnsServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/match-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody()
            .jsonPath("$.error").isEqualTo(true)
            .jsonPath("$.service").isEqualTo("match-service")
            .jsonPath("$.message").isEqualTo("Match Service temporalmente no disponible")
            .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
            .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    void bffFallbackReturnsServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/bff")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody()
            .jsonPath("$.error").isEqualTo(true)
            .jsonPath("$.service").isEqualTo("bff")
            .jsonPath("$.message").isEqualTo("BFF Service temporalmente no disponible")
            .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
            .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    void fallbackHealthReturnsOk() {
        webTestClient.get()
            .uri("/fallback/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("fallback-active")
            .jsonPath("$.message").isEqualTo("Algunos servicios pueden estar temporalmente no disponibles")
            .jsonPath("$.timestamp").isNotEmpty();
    }
}
