package com.example.moviequizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultDTO {

    private boolean correct;
    private QuestionDTO nextQuestion; // will be null if incorrect or game ends

}
