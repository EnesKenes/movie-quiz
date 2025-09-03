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
@Schema(description = "Result returned after submitting an answer")
public class AnswerResultDTO {

    @Schema(description = "Whether the submitted answer was correct", example = "true")
    private boolean correct;

    @Schema(description = "Next question to answer if the previous one was correct; null otherwise")
    private QuestionDTO nextQuestion;
}
