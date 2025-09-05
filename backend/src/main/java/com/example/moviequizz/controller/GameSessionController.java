package com.example.moviequizz.controller;

import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameSessionDTO> startGame(@RequestParam String username) {
        return ResponseEntity.ok(gameSessionService.startGame(username));
    }

    @PostMapping("/{sessionId}/score")
    public ResponseEntity<GameSessionDTO> updateScore(
            @PathVariable String sessionId,
            @RequestParam int score
    ) {
        return ResponseEntity.ok(gameSessionService.updateScore(sessionId, score));
    }

    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<GameSessionDTO> finishGame(@PathVariable String sessionId) {
        return ResponseEntity.ok(gameSessionService.finishGame(sessionId));
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<GameSessionDTO>> getTopScores(@PathVariable int limit) {
        return ResponseEntity.ok(gameSessionService.getTopScores(limit));
    }
}
