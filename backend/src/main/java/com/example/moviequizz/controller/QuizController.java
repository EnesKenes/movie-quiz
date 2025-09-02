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

    @GetMapping("/question")
    public QuestionDTO getQuestion() {
        return quizService.generateQuestion();
    }

    @PostMapping("/answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerResultDTO result = quizService.submitAnswer(answerDTO);
        return ResponseEntity.ok(result);
    }
}
