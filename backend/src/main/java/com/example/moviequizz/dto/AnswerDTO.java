package com.example.moviequizz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Answer submitted by the player")
public class AnswerDTO {

    @Schema(description = "The answer selected by the player", example = "Christopher Nolan")
    private String selectedAnswer;

    @Schema(
            description =
                    "The token identifying the question (usually a JWT containing the IMDb ID)",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0dDAwMTIzNDUifQ.sometokenvalue")
    private String token;
}
