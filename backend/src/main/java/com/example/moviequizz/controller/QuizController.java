package com.example.moviequizz.controller;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movie Quiz API", description = "Endpoints for managing quiz questions and answers")
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @Operation(
            summary = "Get a quiz question",
            description =
                    "Generates and returns a random quiz question. The type of the question (actor,"
                            + " director, genre, or year) is chosen automatically.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "A new question has been generated",
                        content = @Content(schema = @Schema(implementation = QuestionDTO.class)))
            })
    @GetMapping("/question")
    public QuestionDTO getQuestion() {
        return quizService.generateQuestion();
    }

    @Operation(
            summary = "Submit an answer",
            description =
                    "Submits an answer for validation. Returns whether the answer was correct and,"
                            + " if so, also provides the next question.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "The selected answer and its associated token",
                            required = true,
                            content = @Content(schema = @Schema(implementation = AnswerDTO.class))),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Answer evaluated successfully",
                        content =
                                @Content(schema = @Schema(implementation = AnswerResultDTO.class)))
            })
    @PostMapping("/answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerResultDTO result = quizService.submitAnswer(answerDTO);
        return ResponseEntity.ok(result);
    }
}
