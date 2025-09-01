package com.example.moviequizz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDTO {

    private Long id;

    private String username;

    private int score;

    private LocalDateTime createTime;

}
