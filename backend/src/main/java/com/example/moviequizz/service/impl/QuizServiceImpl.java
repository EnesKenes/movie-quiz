package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.util.JwtUtil;
import com.example.moviequizz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class QuizServiceImpl implements QuizService {

    private final MovieRepository movieRepository;
    private final JwtUtil jwtUtil;
    private final Random random = new Random();

    @Autowired
    public QuizServiceImpl(MovieRepository movieRepository, JwtUtil jwtUtil) {
        this.movieRepository = movieRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public QuestionDTO generateQuestion() {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) return null;

        // Pick a random movie
        Movie movie = movies.get(random.nextInt(movies.size()));

        // Pick one of the directors as the correct answer
        String correctDirector = movie.getDirectors().get(random.nextInt(movie.getDirectors().size()));
        String title = movie.getTitle();
        String imdbID = movie.getImdbId();

        // Collect all directors for wrong options
        List<String> allDirectors = new ArrayList<>();
        for (Movie m : movies) {
            allDirectors.addAll(m.getDirectors());
        }

        List<String> options = new ArrayList<>();
        options.add(correctDirector);

        // Pick 3 other random directors
        while (options.size() < 4 && !allDirectors.isEmpty()) {
            String director = allDirectors.get(random.nextInt(allDirectors.size()));
            if (!options.contains(director)) {
                options.add(director);
            }
        }

        Collections.shuffle(options);

        String questionText = "Who directed the movie " + title + " ?";
        String token = jwtUtil.generateQuestionToken(imdbID, correctDirector, 10 * 60 * 1000); // 10 min

        return new QuestionDTO(questionText, options, token);
    }
}

