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

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<GameSessionDTO>> getTopScores(@PathVariable int limit) {
        return ResponseEntity.ok(gameSessionService.getTopScores(limit));
    }
}
