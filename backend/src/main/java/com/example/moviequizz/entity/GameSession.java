package com.example.moviequizz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private UUID gameId; // UUID stored as String

    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private boolean finished = false;

    private LocalDateTime finishedAt;

    public GameSession(String username) {
        this.username = username;
        this.gameId = UUID.randomUUID();
        this.score = 0;
        this.finished = false;
    }

}
