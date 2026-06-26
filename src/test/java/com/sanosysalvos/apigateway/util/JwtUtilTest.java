package com.sanosysalvos.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "1234567890123456789012345678901234567890";

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        Field field = JwtUtil.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(jwtUtil, SECRET);
        jwtUtil.init();
    }

    private String crearTokenValido() {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .setSubject("admin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String crearTokenExpirado() {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .setSubject("admin")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void tokenValido_DeberiaRetornarTrue() {
        assertTrue(jwtUtil.isTokenValid(crearTokenValido()));
    }

    @Test
    void tokenInvalido_DeberiaRetornarFalse() {
        assertFalse(jwtUtil.isTokenValid("token.invalido.xyz"));
    }

    @Test
    void tokenExpirado_DeberiaRetornarFalse() {
        assertFalse(jwtUtil.isTokenValid(crearTokenExpirado()));
    }

    @Test
    void tokenVacio_DeberiaRetornarFalse() {
        assertFalse(jwtUtil.isTokenValid(""));
    }

    @Test
    void tokenNulo_DeberiaRetornarFalse() {
        assertFalse(jwtUtil.isTokenValid(null));
    }

    @Test
    void getAllClaims_DeberiaRetornarSubject() {
        String token = crearTokenValido();
        assertEquals("admin", jwtUtil.getAllClaimsFromToken(token).getSubject());
    }

    @Test
    void getAllClaims_ConTokenInvalido_DeberiaLanzarExcepcion() {
        assertThrows(Exception.class, () -> jwtUtil.getAllClaimsFromToken("token.invalido"));
    }
}
