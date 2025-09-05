package com.example.moviequizz.repository;

import com.example.moviequizz.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findByGameId(UUID gameId);

    @Query("SELECT g FROM GameSession g WHERE g.finished = true ORDER BY g.score DESC")
    List<GameSession> findTopFinishedSessions(org.springframework.data.domain.Pageable pageable);

}
