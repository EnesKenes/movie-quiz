package com.example.moviequizz.repository;

import com.example.moviequizz.entity.Score;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findAllByOrderByScoreDesc(Pageable pageable);
}
