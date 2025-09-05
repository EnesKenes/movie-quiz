package com.example.moviequizz.service;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.dto.QuestionType;

public interface QuizService {

    QuestionDTO startNewGame(String username);

    QuestionDTO generateQuestion(QuestionType type);

    AnswerResultDTO submitAnswer(AnswerDTO answerDTO);

}
