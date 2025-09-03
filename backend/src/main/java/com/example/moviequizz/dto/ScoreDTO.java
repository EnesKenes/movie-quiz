package com.example.moviequizz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Score entry representing a player's result")
public class ScoreDTO {

    @Schema(description = "Unique identifier of the score entry", example = "123")
    private Long id;

    @Schema(description = "Player's username", example = "MovieFan99")
    private String username;

    @Schema(description = "Player's score", example = "85")
    private int score;

    @Schema(description = "Timestamp when the score was recorded", example = "2025-09-03T12:34:56")
    private LocalDateTime createTime;

}
