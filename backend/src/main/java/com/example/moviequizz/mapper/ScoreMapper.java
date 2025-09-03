package com.example.moviequizz.mapper;

import com.example.moviequizz.dto.ScoreDTO;
import com.example.moviequizz.entity.Score;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

    ScoreDTO toDTO(Score score);

    Score toEntity(ScoreDTO scoreDTO);
}
