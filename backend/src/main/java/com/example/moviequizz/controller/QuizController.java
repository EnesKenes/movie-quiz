package com.example.moviequizz.controller;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Start a new game for a user and return the first question.
     * The frontend should send the username when starting a game.
     */
    @PostMapping("/start")
    public ResponseEntity<QuestionDTO> startNewGame(@RequestParam String username) {
        QuestionDTO firstQuestion = quizService.startNewGame(username);
        return ResponseEntity.ok(firstQuestion);
    }

    /**
     * Submit an answer for a given game.
     */
    @PostMapping("/answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerResultDTO result = quizService.submitAnswer(answerDTO);
        return ResponseEntity.ok(result);
    }
}
