package com.example.moviequizz.service;

import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.entity.GameSession;

import java.util.List;

public interface GameSessionService {

    GameSessionDTO startGame(String username);

    GameSessionDTO updateScore(String sessionId, int newScore);

    GameSessionDTO finishGame(String sessionId);

    List<GameSessionDTO> getTopScores(int limit);

}
