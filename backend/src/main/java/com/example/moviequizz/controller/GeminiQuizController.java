package com.example.moviequizz.controller;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.service.GeminiQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini-quiz")
public class GeminiQuizController {

    private final GeminiQuizService geminiQuizService;

    @Autowired
    public GeminiQuizController(GeminiQuizService geminiQuizService) {
        this.geminiQuizService = geminiQuizService;
    }

    /**
     * Start a new Gemini-based game for a user and return the first question.
     */
    @PostMapping("/start")
    public ResponseEntity<QuestionDTO> startNewGame(@RequestParam String username) {
        QuestionDTO firstQuestion = geminiQuizService.startNewGame(username);
        return ResponseEntity.ok(firstQuestion);
    }

    /**
     * Submit an answer for a Gemini-based game.
     */
    @PostMapping("/answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerResultDTO result = geminiQuizService.submitAnswer(answerDTO);
        return ResponseEntity.ok(result);
    }
}
