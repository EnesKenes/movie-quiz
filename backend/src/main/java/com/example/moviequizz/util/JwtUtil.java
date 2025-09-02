package com.example.moviequizz.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {


    private final SecretKey key;

    public JwtUtil(@Value("${app.jwt.secret}") String base64Secret) {
        this.key = Keys.hmacShaKeyFor(base64Secret.getBytes());
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

    // Extract Claims from token using parseSignedClaims (works in 0.12.6)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract the correct answer
    public String extractCorrectAnswer(String token) {
        return extractAllClaims(token).get("correctAnswer", String.class);
    }

    // Check if token expired
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Validate answer
    public boolean validateAnswer(String token, String selectedAnswer) {
        try {
            String correctAnswer = extractCorrectAnswer(token);
            if (correctAnswer == null || isTokenExpired(token)) {
                return false;
            }

            // trim + ignore case to avoid whitespace/case issues
            return correctAnswer.trim().equalsIgnoreCase(selectedAnswer.trim());
        } catch (Exception e) {
            return false; // token invalid, expired, or tampered
        }
    }
}
