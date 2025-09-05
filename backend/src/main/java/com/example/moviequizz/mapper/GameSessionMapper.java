package com.example.moviequizz.mapper;

import com.example.moviequizz.dto.GameSessionDTO;
import com.example.moviequizz.entity.GameSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameSessionMapper {

    GameSessionDTO toDTO(GameSession gameSession);

//    GameSession toEntity(GameSessionDTO gameSessionDTO);

}
