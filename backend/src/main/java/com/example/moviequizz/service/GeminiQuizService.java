package com.example.moviequizz.service;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;

public interface GeminiQuizService {

    QuestionDTO startNewGame(String username);

    AnswerResultDTO submitAnswer(AnswerDTO answerDTO);

}
