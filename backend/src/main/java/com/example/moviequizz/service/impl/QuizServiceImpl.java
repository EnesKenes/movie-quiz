package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.dto.QuestionType;
import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.GameSessionRepository;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.service.QuizService;
import com.example.moviequizz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private final MovieRepository movieRepository;
    private final GameSessionRepository gameSessionRepository;
    private final JwtUtil jwtUtil;
    private final Random random = new Random();

    @Autowired
    public QuizServiceImpl(
            MovieRepository movieRepository,
            GameSessionRepository gameSessionRepository,
            JwtUtil jwtUtil
    ) {
        this.movieRepository = movieRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public QuestionDTO startNewGame(String username) {
        // create new game session
        GameSession session = GameSession.builder()
                .gameId(UUID.randomUUID())
                .username(username)
                .score(0)
                .finished(false)
                .finishedAt(null)
                .build();

        gameSessionRepository.save(session);

        // return first question with gameId included
        QuestionDTO question = generateQuestion(null);
        question.setGameId(session.getGameId());
        return question;
    }

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

        return QuestionDTO.builder()
                .questionText(questionText)
                .options(options)
                .token(token)
                .type(type)
                .imageUrl(movie.getImageUrl())
                .build();
    }

    private String pickRandomFromCsv(String csv) {
        String[] values = csv.split(",\\s*");
        return values[random.nextInt(values.length)];
    }

    private List<String> generateRandomOptions(
            String correctAnswer, QuestionType type, List<Movie> movies, Movie correctMovie) {

        Set<String> wrongOptions = new HashSet<>();
        Set<String> excluded = new HashSet<>();

        switch (type) {
            case DIRECTOR -> excluded.addAll(Arrays.asList(correctMovie.getDirectorsCsv().split(",\\s*")));
            case ACTOR -> excluded.addAll(Arrays.asList(correctMovie.getActorsCsv().split(",\\s*")));
            case GENRE -> excluded.addAll(Arrays.asList(correctMovie.getGenresCsv().split(",\\s*")));
            case YEAR -> excluded.add(String.valueOf(correctMovie.getYear()));
        }

        while (wrongOptions.size() < 3) {
            Movie randomMovie = movies.get(random.nextInt(movies.size()));
            String option;

            switch (type) {
                case DIRECTOR -> option = pickRandomFromCsv(randomMovie.getDirectorsCsv());
                case ACTOR -> option = pickRandomFromCsv(randomMovie.getActorsCsv());
                case GENRE -> option = pickRandomFromCsv(randomMovie.getGenresCsv());
                case YEAR -> option = String.valueOf(randomMovie.getYear());
                default -> {
                    continue;
                }
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
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        Optional<Movie> movieOpt = movieRepository.findByImdbId(questionId);
        if (movieOpt.isEmpty()) {
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        Movie movie = movieOpt.get();
        QuestionType type = jwtUtil.extractQuestionType(answerDTO.getToken());

        boolean correct;

        switch (type) {
            case DIRECTOR ->
                    correct = containsCsvValue(movie.getDirectorsCsv(), answerDTO.getSelectedAnswer());
            case ACTOR ->
                    correct = containsCsvValue(movie.getActorsCsv(), answerDTO.getSelectedAnswer());
            case GENRE ->
                    correct = containsCsvValue(movie.getGenresCsv(), answerDTO.getSelectedAnswer());
            case YEAR ->
                    correct = String.valueOf(movie.getYear())
                            .equalsIgnoreCase(answerDTO.getSelectedAnswer().trim());
            default -> {
                return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
            }
        }

        // fetch game session by gameId
        UUID gameId = answerDTO.getGameId();
        Optional<GameSession> sessionOpt = gameSessionRepository.findByGameId(gameId);
        if (sessionOpt.isEmpty()) {
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        GameSession session = sessionOpt.get();

        if (!correct) {
            // mark game finished
            session.setFinished(true);
            session.setFinishedAt(LocalDateTime.now());
            gameSessionRepository.save(session);

            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        // correct answer -> increase score
        session.setScore(session.getScore() + 1);
        gameSessionRepository.save(session);

        QuestionDTO nextQuestion = generateQuestion();
        nextQuestion.setGameId(session.getGameId());

        return AnswerResultDTO.builder().correct(true).nextQuestion(nextQuestion).build();
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
