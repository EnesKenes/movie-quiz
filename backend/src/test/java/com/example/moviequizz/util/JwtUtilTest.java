package com.example.moviequizz.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        String secret = "replace_this_with_a_very_long_secret_key_for_jwt_purposes";
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    public void testGenerateAndValidateToken() {
        String questionId = "q123";
        String correctAnswer = "Inception";
        long expirationMs = 60_000; // 1 minute

        // Generate token
        String token = jwtUtil.generateQuestionToken(questionId, correctAnswer, expirationMs);
        assertNotNull(token);

        // Extract correct answer
        String extractedAnswer = jwtUtil.extractCorrectAnswer(token);
        assertEquals(correctAnswer, extractedAnswer);

        // Validate correct answer
        boolean valid = jwtUtil.validateAnswer(token, correctAnswer);
        assertTrue(valid);

        // Validate wrong answer
        boolean invalid = jwtUtil.validateAnswer(token, "Titanic");
        assertFalse(invalid);
    }

    @Test
    public void testExpiredToken() throws InterruptedException {
        String token = jwtUtil.generateQuestionToken("q1", "Answer", 1000); // 1 second
        Thread.sleep(1500); // wait for expiration
        boolean valid = jwtUtil.validateAnswer(token, "Answer");
        assertFalse(valid);
    }
}
