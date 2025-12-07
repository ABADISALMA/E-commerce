package com.example.Payment_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MaCleSuperSecretePourJWT_ChangeMoi1234567890123456789";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);

        // ⚠️ CRITIQUE: Vérifier que le rôle commence par "ROLE_"
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
            System.out.println("⚠️ Ajout du préfixe ROLE_ au rôle: " + role);
        }

        System.out.println("🔑 Rôle extrait du token: " + role);
        return role;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            boolean expired = isTokenExpired(token);
            System.out.println("🔍 Validation token - Expiré: " + expired);
            return !expired;
        } catch (Exception e) {
            System.out.println("❌ Erreur validation token: " + e.getMessage());
            return false;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}