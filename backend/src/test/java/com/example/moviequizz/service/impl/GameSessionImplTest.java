package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.mapper.GameSessionMapper;
import com.example.moviequizz.repository.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSessionImplTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private GameSessionMapper gameSessionMapper;

    @InjectMocks
    private GameSessionImpl gameSessionService;

    private GameSession session;
    private GameSessionDTO sessionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = GameSession.builder()
                .gameId(UUID.randomUUID())
                .username("user1")
                .score(0)
                .finishedAt(null)
                .build();

        sessionDTO = GameSessionDTO.builder()
                .gameId(session.getGameId())
                .username(session.getUsername())
                .score(session.getScore())
                .finishedAt(session.getFinishedAt())
                .build();
    }

    @Test
    void testStartGame_createsSession() {
        when(gameSessionRepository.save(any())).thenReturn(session);
        when(gameSessionMapper.toDTO(session)).thenReturn(sessionDTO);

        GameSessionDTO result = gameSessionService.startGame("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        verify(gameSessionRepository).save(any());
        verify(gameSessionMapper).toDTO(session);
    }

    @Test
    void testUpdateScore_updatesScore() {
        when(gameSessionRepository.findByGameId(session.getGameId())).thenReturn(Optional.of(session));
        when(gameSessionRepository.save(session)).thenReturn(session);
        when(gameSessionMapper.toDTO(session)).thenReturn(sessionDTO);

        GameSessionDTO result = gameSessionService.updateScore(session.getGameId(), 10);

        assertEquals(10, session.getScore());
        assertEquals(sessionDTO, result);
        verify(gameSessionRepository).findByGameId(session.getGameId());
        verify(gameSessionRepository).save(session);
        verify(gameSessionMapper).toDTO(session);
    }

    @Test
    void testUpdateScore_invalidGameId_throwsException() {
        UUID invalidId = UUID.randomUUID();
        when(gameSessionRepository.findByGameId(invalidId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> gameSessionService.updateScore(invalidId, 10));
    }

    @Test
    void testFinishGame_setsFinishedAt() {
        when(gameSessionRepository.findByGameId(session.getGameId())).thenReturn(Optional.of(session));
        when(gameSessionRepository.save(session)).thenReturn(session);
        when(gameSessionMapper.toDTO(session)).thenReturn(sessionDTO);

        GameSessionDTO result = gameSessionService.finishGame(session.getGameId());

        assertNotNull(session.getFinishedAt());
        assertEquals(sessionDTO, result);
        verify(gameSessionRepository).save(session);
        verify(gameSessionMapper).toDTO(session);
    }

    @Test
    void testFinishGame_invalidGameId_throwsException() {
        UUID invalidId = UUID.randomUUID();
        when(gameSessionRepository.findByGameId(invalidId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> gameSessionService.finishGame(invalidId));
    }

    @Test
    void testGetTopScores_returnsList() {
        List<GameSession> sessions = List.of(session);
        when(gameSessionRepository.findTopFinishedSessions(PageRequest.of(0, 5))).thenReturn(sessions);
        when(gameSessionMapper.toDTO(session)).thenReturn(sessionDTO);

        List<GameSessionDTO> result = gameSessionService.getTopScores(5);

        assertEquals(1, result.size());
        assertEquals(sessionDTO, result.get(0));
        verify(gameSessionRepository).findTopFinishedSessions(PageRequest.of(0, 5));
        verify(gameSessionMapper).toDTO(session);
    }
}
