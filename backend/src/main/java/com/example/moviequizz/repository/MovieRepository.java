package com.example.moviequizz.repository;

import com.example.moviequizz.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByImdbId(String imdbId);

    Optional<Movie> findByImdbId(String imdbId);

}
