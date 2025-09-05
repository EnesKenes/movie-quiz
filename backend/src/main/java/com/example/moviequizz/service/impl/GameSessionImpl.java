package com.example.moviequizz.service.impl;

import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.mapper.GameSessionMapper;
import com.example.moviequizz.repository.GameSessionRepository;
import com.example.moviequizz.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameSessionImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSessionMapper gameSessionMapper;

    @Autowired
    public GameSessionImpl(GameSessionRepository gameSessionRepository, GameSessionMapper gameSessionMapper) {
        this.gameSessionRepository = gameSessionRepository;
        this.gameSessionMapper = gameSessionMapper;
    }

    @Override
    public GameSessionDTO startGame(String username) {
        GameSession session = GameSession.builder()
                .gameId(UUID.randomUUID())
                .username(username)
                .score(0)
                .finished(false)
                .finishedAt(null)
                .build();

        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    @Override
    public GameSessionDTO updateScore(UUID gameId, int newScore) {
        GameSession session = gameSessionRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        session.setScore(newScore);
        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    @Override
    public GameSessionDTO finishGame(UUID gameId) {
        GameSession session = gameSessionRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        session.setFinished(true);
        session.setFinishedAt(LocalDateTime.now());
        return gameSessionMapper.toDTO(gameSessionRepository.save(session));
    }

    @Override
    public List<GameSessionDTO> getTopScores(int limit) {
        List<GameSession> sessions = gameSessionRepository.findTopFinishedSessions(PageRequest.of(0, limit));
        return sessions.stream()
                .map(gameSessionMapper::toDTO)
                .toList();
    }
}
