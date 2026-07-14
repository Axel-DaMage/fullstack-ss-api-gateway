package com.sanosysalvos.apigateway.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Key key;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        var secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "1234567890123456789012345678901234567890");

        var initMethod = JwtUtil.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(jwtUtil);

        key = Keys.hmacShaKeyFor("1234567890123456789012345678901234567890".getBytes());
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        String token = Jwts.builder()
                .setSubject("admin")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    void isTokenValid_WithEmptyToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.isTokenValid(""));
    }

    @Test
    void getAllClaimsFromToken_ShouldReturnCorrectSubject() {
        String token = Jwts.builder()
                .setSubject("admin")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        var claims = jwtUtil.getAllClaimsFromToken(token);
        assertEquals("admin", claims.getSubject());
    }

    @Test
    void getAllClaimsFromToken_WithInvalidToken_ShouldThrow() {
        assertThrows(Exception.class, () -> jwtUtil.getAllClaimsFromToken("bad.token.here"));
    }
}
