package com.example.VideoService.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    private String secret;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = deriveSecretKey(secret);
    }

    private SecretKey deriveSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return new SecretKeySpec(paddedKey, "HmacSHA256");
        } else if (keyBytes.length > 32) {
            return new SecretKeySpec(keyBytes, 0, 32, "HmacSHA256");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
