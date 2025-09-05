package com.example.moviequizz.service;

import com.example.moviequizz.dto.GameSessionDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionService {

    GameSessionDTO startGame(String username);

    GameSessionDTO updateScore(UUID gameId, int newScore);

    GameSessionDTO finishGame(UUID gameId);

    List<GameSessionDTO> getTopScores(int limit);

}
