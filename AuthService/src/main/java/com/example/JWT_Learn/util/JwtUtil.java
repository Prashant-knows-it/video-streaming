package com.example.JWT_Learn.util;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {
    private String secret;
    private long expiration;
    private long refreshExpiration;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = deriveSecretKey(secret);
    }

    private SecretKey deriveSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        // Ensure the key is at least 32 bytes for HMAC SHA-256
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return new SecretKeySpec(paddedKey, "HmacSHA256");
        } else if (keyBytes.length > 32) {
            return new SecretKeySpec(keyBytes, 0, 32, "HmacSHA256");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(Authentication authentication) {
        return createToken(authentication.getName(), authentication.getAuthorities(), expiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        return createToken(authentication.getName(), authentication.getAuthorities(), refreshExpiration);
    }

    private String createToken(String username, Collection<? extends GrantedAuthority> authorities, long expirationTime) {
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Getters & Setters for @ConfigurationProperties
    public void setSecret(String secret) {
        this.secret = secret;
        this.secretKey = deriveSecretKey(secret); // Update if changed at runtime
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
