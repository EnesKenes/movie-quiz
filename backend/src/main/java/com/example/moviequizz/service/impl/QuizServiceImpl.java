package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.dto.QuestionType;
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
        return generateQuestion(null);
    }

    public QuestionDTO generateQuestion(QuestionType type) {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) return null;

        if (type == null) {
            QuestionType[] values = QuestionType.values();
            type = values[random.nextInt(values.length)];
        }

        return generateQuestionOfType(type, movies);
    }

    private QuestionDTO generateQuestionOfType(QuestionType type, List<Movie> movies) {
        Movie movie = movies.get(random.nextInt(movies.size()));
        String imdbID = movie.getImdbId();
        String correctAnswer;
        String questionText;

        switch (type) {
            case DIRECTOR:
                correctAnswer = pickRandomFromCsv(movie.getDirectorsCsv());
                questionText = "Who directed the movie " + movie.getTitle() + "?";
                break;
            case ACTOR:
                correctAnswer = pickRandomFromCsv(movie.getActorsCsv());
                questionText = "Which actor starred in " + movie.getTitle() + "?";
                break;
            case GENRE:
                correctAnswer = pickRandomFromCsv(movie.getGenresCsv());
                questionText = "What is the genre of " + movie.getTitle() + "?";
                break;
            case YEAR:
                correctAnswer = String.valueOf(movie.getYear());
                questionText = "In which year was " + movie.getTitle() + " released?";
                break;
            default:
                throw new IllegalArgumentException("Unsupported question type");
        }

        List<String> options = generateRandomOptions(correctAnswer, type, movies, movie);
        Collections.shuffle(options);

        String token = jwtUtil.generateQuestionToken(imdbID, type, 10 * 60 * 1000);

        return new QuestionDTO(questionText, options, token, type);
    }

    private String pickRandomFromCsv(String csv) {
        String[] values = csv.split(",\\s*");
        return values[random.nextInt(values.length)];
    }

    private List<String> generateRandomOptions(String correctAnswer, QuestionType type, List<Movie> movies, Movie correctMovie) {
        Set<String> wrongOptions = new HashSet<>();
        Set<String> excluded = new HashSet<>();

        switch (type) {
            case DIRECTOR:
                excluded.addAll(Arrays.asList(correctMovie.getDirectorsCsv().split(",\\s*")));
                break;
            case ACTOR:
                excluded.addAll(Arrays.asList(correctMovie.getActorsCsv().split(",\\s*")));
                break;
            case GENRE:
                excluded.addAll(Arrays.asList(correctMovie.getGenresCsv().split(",\\s*")));
                break;
            case YEAR:
                excluded.add(String.valueOf(correctMovie.getYear()));
                break;
        }

        while (wrongOptions.size() < 3) {
            Movie randomMovie = movies.get(random.nextInt(movies.size()));
            String option;

            switch (type) {
                case DIRECTOR:
                    option = pickRandomFromCsv(randomMovie.getDirectorsCsv());
                    break;
                case ACTOR:
                    option = pickRandomFromCsv(randomMovie.getActorsCsv());
                    break;
                case GENRE:
                    option = pickRandomFromCsv(randomMovie.getGenresCsv());
                    break;
                case YEAR:
                    option = String.valueOf(randomMovie.getYear());
                    break;
                default:
                    continue;
            }

            if (!excluded.contains(option)) {
                wrongOptions.add(option);
            }
        }

        List<String> options = new ArrayList<>(wrongOptions);
        options.add(correctAnswer);
        return options;
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
        QuestionType type = jwtUtil.extractQuestionType(answerDTO.getToken());

        boolean correct;

        switch (type) {
            case DIRECTOR:
                correct = containsCsvValue(movie.getDirectorsCsv(), answerDTO.getSelectedAnswer());
                break;
            case ACTOR:
                correct = containsCsvValue(movie.getActorsCsv(), answerDTO.getSelectedAnswer());
                break;
            case GENRE:
                correct = containsCsvValue(movie.getGenresCsv(), answerDTO.getSelectedAnswer());
                break;
            case YEAR:
                correct = String.valueOf(movie.getYear())
                        .equalsIgnoreCase(answerDTO.getSelectedAnswer().trim());
                break;
            default:
                return new AnswerResultDTO(false, null);
        }

        if (!correct) return new AnswerResultDTO(false, null);

        QuestionDTO nextQuestion = generateQuestion();
        return new AnswerResultDTO(true, nextQuestion);
    }

    /**
     * Returns true if the comma-separated string contains the answer (ignoring case and trimming spaces)
     */
    private boolean containsCsvValue(String csv, String answer) {
        if (csv == null || csv.isBlank() || answer == null) return false;

        String[] values = csv.split(",\\s*");
        for (String val : values) {
            if (val.trim().equalsIgnoreCase(answer.trim())) return true;
        }
        return false;
    }

}
