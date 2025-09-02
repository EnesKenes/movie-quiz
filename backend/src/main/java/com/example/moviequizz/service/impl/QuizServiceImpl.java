package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.util.JwtUtil;
import com.example.moviequizz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        String correctDirector = movie.getDirectors().get(random.nextInt(movie.getDirectors().size()));
        String title = movie.getTitle();
        String imdbID = movie.getImdbId();

        // Collect wrong directors
        Set<String> wrongDirectors = new HashSet<>();
        while (wrongDirectors.size() < 3) {
            Movie randomMovie = movies.get(random.nextInt(movies.size()));
            String director = randomMovie.getDirectors().get(random.nextInt(randomMovie.getDirectors().size()));
            if (!director.equals(correctDirector)) {
                wrongDirectors.add(director);
            }
        }

        // Build options list
        List<String> options = new ArrayList<>(wrongDirectors);
        options.add(correctDirector);
        Collections.shuffle(options);

        String questionText = "Who directed the movie " + title + " ?";
        String token = jwtUtil.generateQuestionToken(imdbID, 10 * 60 * 1000); // 10 min

        return new QuestionDTO(questionText, options, token);
    }

    @Override
    public AnswerResultDTO submitAnswer(AnswerDTO answerDTO) {
        String questionId = jwtUtil.extractQuestionId(answerDTO.getToken());

        if (jwtUtil.isTokenExpired(answerDTO.getToken())) {
            return new AnswerResultDTO(false, null);
        }

        Optional<Movie> movieOpt = movieRepository.findByImdbId(questionId);
        if (movieOpt.isEmpty()) return new AnswerResultDTO(false, null);

        Movie movie = movieOpt.get();
        String correctDirector = movie.getDirectors().getFirst(); // or choose logic for multiple directors

        boolean correct = correctDirector.trim().equalsIgnoreCase(answerDTO.getSelectedAnswer().trim());

        if (!correct) return new AnswerResultDTO(false, null);

        // Correct -> generate next question
        QuestionDTO nextQuestion = generateQuestion();
        return new AnswerResultDTO(true, nextQuestion);
    }
}
