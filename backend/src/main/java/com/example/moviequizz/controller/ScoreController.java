package com.example.moviequizz.controller;

import com.example.moviequizz.dto.ScoreDTO;
import com.example.moviequizz.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    public ScoreDTO submitScore(@RequestBody ScoreDTO scoreDTO) {
        return scoreService.saveScore(scoreDTO);
    }

    @GetMapping("/top/{limit}")
    public List<ScoreDTO> getTopScores(@PathVariable int limit) {
        return scoreService.getTopScores(limit);
    }
}
