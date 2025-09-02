package com.example.moviequizz.util;

import com.example.moviequizz.dto.QuestionType;
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

    // Generate token with questionId and type
    public String generateQuestionToken(String questionId, QuestionType type, long expirationMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(questionId)
                .claim("questionType", type.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract the question ID from token
    public String extractQuestionId(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract question type from token
    public QuestionType extractQuestionType(String token) {
        String typeStr = extractAllClaims(token).get("questionType", String.class);
        if (typeStr == null) {
            throw new IllegalArgumentException("Token does not contain a question type");
        }
        return QuestionType.valueOf(typeStr);
    }

    // Check if token expired
    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

