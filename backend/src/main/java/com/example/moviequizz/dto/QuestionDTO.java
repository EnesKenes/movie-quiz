package com.example.moviequizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private String questionText;
    private List<String> options;
    private String token; // JWT containing the correct answer

}
