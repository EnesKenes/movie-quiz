package com.example.moviequizz.controller;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.service.QuizService;
import com.example.moviequizz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final JwtUtil jwtUtil;

    @Autowired
    public QuizController(QuizService quizService, JwtUtil jwtUtil) {
        this.quizService = quizService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/question")
    public QuestionDTO getQuestion() {
        return quizService.generateQuestion();
    }

    @PostMapping("/answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO answerDTO) {
        boolean correct = jwtUtil.validateAnswer(answerDTO.getToken(), answerDTO.getSelectedAnswer());

        if (!correct) {
            // Wrong answer -> game over, no next question
            return ResponseEntity.ok(new AnswerResultDTO(false, null));
        }

        // Correct -> fetch next question
        QuestionDTO nextQuestion = quizService.generateQuestion();
        return ResponseEntity.ok(new AnswerResultDTO(true, nextQuestion));
    }

}
