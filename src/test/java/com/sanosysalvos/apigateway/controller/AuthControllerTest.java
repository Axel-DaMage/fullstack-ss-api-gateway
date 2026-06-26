package com.sanosysalvos.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"eureka.client.enabled=false"})
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void loginConCredencialesValidas_DeberiaRetornar200YToken() {
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
    void loginConPasswordIncorrecto_DeberiaRetornar401() {
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
    void loginConUsernameIncorrecto_DeberiaRetornar401() {
        webTestClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"username\":\"hacker\",\"password\":\"admin\"}")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void loginConCuerpoVacio_DeberiaRetornar400() {
        webTestClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
