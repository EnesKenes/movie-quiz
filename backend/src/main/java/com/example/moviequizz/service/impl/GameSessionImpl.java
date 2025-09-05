package com.example.moviequizz.service.impl;

import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.mapper.GameSessionMapper;
import com.example.moviequizz.repository.GameSessionRepository;
import com.example.moviequizz.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameSessionImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSessionMapper gameSessionMapper;

    @Autowired
    public GameSessionImpl(GameSessionRepository gameSessionRepository, GameSessionMapper gameSessionMapper) {
        this.gameSessionRepository = gameSessionRepository;
        this.gameSessionMapper = gameSessionMapper;
    }

    public GameSessionDTO startGame(String username) {
        GameSession session = new GameSession(username);
        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    public GameSessionDTO updateScore(String sessionId, int newScore) {
        GameSession session = gameSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sessionId"));
        session.setScore(newScore);
        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    public GameSessionDTO finishGame(String sessionId) {
        GameSession session = gameSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sessionId"));
        session.setFinished(true);
        session.setFinishedAt(LocalDateTime.now());
        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    public List<GameSessionDTO> getTopScores(int limit) {
        return gameSessionRepository.findAll().stream()
                .filter(GameSession::isFinished)
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .map(gameSessionMapper::toDTO)
                .toList();
    }

}
