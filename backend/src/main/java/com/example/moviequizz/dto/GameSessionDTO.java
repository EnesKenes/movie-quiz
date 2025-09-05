package com.example.moviequizz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSessionDTO {

    private Long id;

    private String username;

    private int score;

    private LocalDateTime finishedAt;

    private UUID gameId;

}
