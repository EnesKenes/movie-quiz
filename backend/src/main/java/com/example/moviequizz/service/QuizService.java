package com.example.moviequizz.service;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;

public interface QuizService {

    QuestionDTO generateQuestion();

    AnswerResultDTO submitAnswer(AnswerDTO answerDTO);

}
