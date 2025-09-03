package com.example.moviequizz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A quiz question presented to the player")
public class QuestionDTO {

    @Schema(description = "The text of the question", example = "Who directed the movie Inception?")
    private String questionText;

    @Schema(
            description = "List of possible answer options",
            example =
                    "[\"Christopher Nolan\", \"Steven Spielberg\", \"Ridley Scott\", \"James"
                            + " Cameron\"]")
    private List<String> options;

    @Schema(
            description = "Token that encodes the question identity (typically a JWT)",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0dDAwMTIzNDUifQ.sometokenvalue")
    private String token;

    @Schema(
            description = "Type of question (e.g., DIRECTOR, ACTOR, GENRE, YEAR)",
            example = "DIRECTOR")
    private QuestionType type;

    @Schema(
            description = "URL of an image related to the question (e.g., movie poster)",
            example = "https://example.com/images/inception.jpg")
    private String imageUrl;
}
