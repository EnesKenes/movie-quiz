package com.example.moviequizz.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        // Decode Base64 secret into HMAC key
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extract correct answer from JWT token
    public String extractCorrectAnswer(String token) {
        return extractClaim(token, claims -> claims.get("correctAnswer", String.class));
    }

    // Extract any claim
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token expired
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Generate a token for a quiz question
    public String generateQuestionToken(String questionId, String correctAnswer, long expirationMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(questionId)
                .claim("correctAnswer", correctAnswer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate a submitted answer against the token
    public Boolean validateAnswer(String token, String selectedAnswer) {
        try {
            return !isTokenExpired(token) &&
                    selectedAnswer.equals(extractCorrectAnswer(token));
        } catch (Exception e) {
            return false; // token invalid, expired, or tampered
        }
    }
}
