package com.example.moviequizz.service;

import com.example.moviequizz.dto.ScoreDTO;

import java.util.List;

public interface ScoreService {

    ScoreDTO saveScore(ScoreDTO scoreDTO);

    List<ScoreDTO> getTopScores(int limit);

}
