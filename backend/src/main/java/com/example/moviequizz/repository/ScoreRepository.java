package com.example.moviequizz.repository;

import com.example.moviequizz.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {

    List<Score> findTopNByOrderByScoreDesc(Pageable pageable);

}
