package com.sanosysalvos.apigateway.filter;

import com.sanosysalvos.apigateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationGatewayFilterFactoryTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private ServerHttpResponse response;
    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationGatewayFilterFactory factory;
    private JwtAuthenticationGatewayFilterFactory.Config config;

    @BeforeEach
    void setUp() {
        factory = new JwtAuthenticationGatewayFilterFactory(jwtUtil);
        config = new JwtAuthenticationGatewayFilterFactory.Config();
        lenient().when(exchange.getRequest()).thenReturn(request);
        lenient().when(exchange.getResponse()).thenReturn(response);
        lenient().when(chain.filter(exchange)).thenReturn(Mono.empty());
    }

    @Test
    void rutaEnSkipPath_DeberiaPasarSinAuth() {
        when(request.getURI()).thenReturn(URI.create("/api/health"));

        Mono<Void> result = factory.apply(config).filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
        verify(jwtUtil, never()).isTokenValid(any());
    }

    @Test
    void rutaProtegida_SinHeaderAuth_DeberiaRetornar401() {
        when(request.getURI()).thenReturn(URI.create("/api/pets/1"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);

        Mono<Void> result = factory.apply(config).filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain, never()).filter(exchange);
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void rutaProtegida_HeaderSinBearer_DeberiaRetornar401() {
        when(request.getURI()).thenReturn(URI.create("/api/matches"));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic dGVzdDp0ZXN0");
        when(request.getHeaders()).thenReturn(headers);
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);

        Mono<Void> result = factory.apply(config).filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rutaProtegida_TokenInvalido_DeberiaRetornar401() {
        when(request.getURI()).thenReturn(URI.create("/api/matching/1"));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer token.invalido");
        when(request.getHeaders()).thenReturn(headers);
        when(jwtUtil.isTokenValid("token.invalido")).thenReturn(false);
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);

        Mono<Void> result = factory.apply(config).filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rutaProtegida_TokenValido_DeberiaPasar() {
        when(request.getURI()).thenReturn(URI.create("/api/locations"));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer token.valido");
        when(request.getHeaders()).thenReturn(headers);
        when(jwtUtil.isTokenValid("token.valido")).thenReturn(true);
        when(request.mutate()).thenReturn(mock(ServerHttpRequest.Builder.class));

        factory.apply(config).filter(exchange, chain);

        verify(chain).filter(exchange);
    }
}
