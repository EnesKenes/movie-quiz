package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.ScoreDTO;
import com.example.moviequizz.entity.Score;
import com.example.moviequizz.mapper.ScoreMapper;
import com.example.moviequizz.repository.ScoreRepository;
import com.example.moviequizz.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;
    private final ScoreMapper scoreMapper;

    @Autowired
    public ScoreServiceImpl(ScoreRepository scoreRepository, ScoreMapper scoreMapper) {
        this.scoreRepository = scoreRepository;
        this.scoreMapper = scoreMapper;
    }

    @Override
    public ScoreDTO saveScore(ScoreDTO scoreDTO) {
        Score entity = scoreMapper.toEntity(scoreDTO);
        Score saved = scoreRepository.save(entity);
        return scoreMapper.toDTO(saved);
    }

    @Override
    public List<ScoreDTO> getTopScores(int limit) {
        return scoreRepository.findTopNByOrderByScoreDesc(PageRequest.of(0, limit))
                .stream()
                .map(scoreMapper::toDTO)
                .collect(Collectors.toList());
    }
}
