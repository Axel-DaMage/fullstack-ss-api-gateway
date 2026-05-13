package com.sanosysalvos.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/pet-service")
    public ResponseEntity<Map<String, Object>> petServiceFallback() {
        return buildFallbackResponse("Pet Service temporalmente no disponible", "pet-service");
    }

    @GetMapping("/geo-service")
    public ResponseEntity<Map<String, Object>> geoServiceFallback() {
        return buildFallbackResponse("Geo Service temporalmente no disponible", "geo-service");
    }

    @GetMapping("/match-service")
    public ResponseEntity<Map<String, Object>> matchServiceFallback() {
        return buildFallbackResponse("Match Service temporalmente no disponible", "match-service");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> fallbackHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "fallback-active",
            "message", "Algunos servicios pueden estar temporalmente no disponibles",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String message, String service) {
        Map<String, Object> body = Map.of(
            "error", true,
            "message", message,
            "service", service,
            "timestamp", LocalDateTime.now().toString(),
            "status", HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}