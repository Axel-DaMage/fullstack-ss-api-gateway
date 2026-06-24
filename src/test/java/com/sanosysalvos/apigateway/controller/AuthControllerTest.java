package com.sanosysalvos.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"admin\",\"password\":\"admin\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty();
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() {
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"admin\",\"password\":\"wrong\"}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }

    @Test
    void login_WithEmptyBody_ShouldReturn401() {
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }
}
