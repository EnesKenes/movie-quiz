package com.example.moviequizz.controller;

import com.example.moviequizz.dto.ScoreDTO;
import com.example.moviequizz.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Score API", description = "Endpoints for submitting and retrieving player scores")
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Operation(
            summary = "Submit a score",
            description = "Submits a player's score after completing a quiz round. "
                    + "The score is persisted in the database.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Score object containing username and score value",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScoreDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Score saved successfully",
                            content = @Content(schema = @Schema(implementation = ScoreDTO.class))
                    )
            }
    )
    @PostMapping
    public ScoreDTO submitScore(@RequestBody ScoreDTO scoreDTO) {
        return scoreService.saveScore(scoreDTO);
    }

    @Operation(
            summary = "Get top scores",
            description = "Retrieves the top scores, limited by the number provided.",
            parameters = {
                    @Parameter(
                            name = "limit",
                            description = "Maximum number of scores to return",
                            example = "10",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of top scores",
                            content = @Content(schema = @Schema(implementation = ScoreDTO.class))
                    )
            }
    )
    @GetMapping("/top/{limit}")
    public List<ScoreDTO> getTopScores(@PathVariable int limit) {
        return scoreService.getTopScores(limit);
    }
}
