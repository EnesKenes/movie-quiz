package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.AnswerDTO;
import com.example.moviequizz.dto.AnswerResultDTO;
import com.example.moviequizz.dto.QuestionDTO;
import com.example.moviequizz.dto.QuestionType;
import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.GameSessionRepository;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.service.GeminiQuizService;
import com.example.moviequizz.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GeminiQuizServiceImpl implements GeminiQuizService {

    private final MovieRepository movieRepository;
    private final GameSessionRepository gameSessionRepository;
    private final JwtUtil jwtUtil;
    private final Random random = new Random();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public GeminiQuizServiceImpl(
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
        // Create new game session
        GameSession session = GameSession.builder()
                .gameId(UUID.randomUUID())
                .username(username)
                .score(0)
                .finished(false)
                .finishedAt(null)
                .build();
        gameSessionRepository.save(session);

        // Return first Gemini question with gameId
        QuestionDTO question = generateQuestion(null);
        question.setGameId(session.getGameId());
        return question;
    }

    public QuestionDTO generateQuestion() {
        return generateQuestion(null);
    }

    public QuestionDTO generateQuestion(QuestionType type) {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) movies = new ArrayList<>(); // Ensure non-null list

        if (type == null) {
            QuestionType[] values = QuestionType.values();
            type = values[random.nextInt(values.length)];
        }

        return generateQuestionOfType(type, movies);
    }

    private QuestionDTO generateQuestionOfType(QuestionType type, List<Movie> movies) {
        try {

            String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

            String prompt = "{ \"contents\": [{ \"parts\": [{\"text\": \"Pick a random movie title that is not a " +
                    "blockbuster or a top-50 most famous movie. The movie can be from any genre and any decade." +
                    " Only give the title, and do not repeat titles you recently suggested.\"}]}]}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-goog-api-key", geminiApiKey);
            HttpEntity<String> request = new HttpEntity<>(prompt, headers);

            ResponseEntity<String> geminiResponse = restTemplate.postForEntity(geminiUrl, request, String.class);
            JsonNode geminiJson = objectMapper.readTree(geminiResponse.getBody());
            String movieTitle = geminiJson.at("/candidates/0/content/parts/0/text").asText().trim();

            // 2. Query OMDB API for full movie info
            String omdbApiKey = System.getenv("OMDB_API_KEY");
            String omdbUrl = "http://www.omdbapi.com/?t=" + movieTitle.replace(" ", "+") + "&apikey=" + omdbApiKey;
            String omdbResponse = restTemplate.getForObject(omdbUrl, String.class);
            JsonNode omdbJson = objectMapper.readTree(omdbResponse);

            if (!omdbJson.has("Title")) {
                throw new RuntimeException("OMDB did not return a valid movie");
            }

            // 3. Save movie to local DB (if not exists)
            String imdbID = omdbJson.has("imdbID") ? omdbJson.get("imdbID").asText() : UUID.randomUUID().toString();
            Optional<Movie> existingMovie = movieRepository.findByImdbId(imdbID);

            Movie movie;
            if (existingMovie.isPresent()) {
                movie = existingMovie.get();
            } else {
                movie = Movie.builder()
                        .imdbId(imdbID)
                        .title(omdbJson.get("Title").asText())
                        .year(omdbJson.has("Year") ? Integer.parseInt(omdbJson.get("Year").asText()) : null)
                        .directorsCsv(omdbJson.has("Director") ? omdbJson.get("Director").asText() : "")
                        .actorsCsv(omdbJson.has("Actors") ? omdbJson.get("Actors").asText() : "")
                        .genresCsv(omdbJson.has("Genre") ? omdbJson.get("Genre").asText() : "")
                        .imageUrl(omdbJson.has("Poster") ? omdbJson.get("Poster").asText() : null)
                        .build();
                movieRepository.save(movie);
            }

            // 4. Generate question like QuizServiceImpl
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

            String token = jwtUtil.generateQuestionToken(movie.getImdbId(), type, 10 * 60 * 1000);

            return QuestionDTO.builder()
                    .questionText(questionText)
                    .options(options)
                    .token(token)
                    .type(type)
                    .imageUrl(movie.getImageUrl())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Gemini question: " + e.getMessage(), e);
        }
    }

    private String pickRandomFromCsv(String csv) {
        String[] values = csv.split(",\\s*");
        return values[random.nextInt(values.length)];
    }

    private List<String> generateRandomOptions(String correctAnswer, QuestionType type, List<Movie> movies, Movie correctMovie) {
        Set<String> wrongOptions = new HashSet<>();
        Set<String> excluded = new HashSet<>();

        switch (type) {
            case DIRECTOR -> excluded.addAll(Arrays.asList(correctMovie.getDirectorsCsv().split(",\\s*")));
            case ACTOR -> excluded.addAll(Arrays.asList(correctMovie.getActorsCsv().split(",\\s*")));
            case GENRE -> excluded.addAll(Arrays.asList(correctMovie.getGenresCsv().split(",\\s*")));
            case YEAR -> excluded.add(String.valueOf(correctMovie.getYear()));
        }

        while (wrongOptions.size() < 3 && !movies.isEmpty()) {
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
        QuestionType type = jwtUtil.extractQuestionType(answerDTO.getToken());

        if (jwtUtil.isTokenExpired(answerDTO.getToken())) {
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        Optional<Movie> movieOpt = movieRepository.findByImdbId(questionId);
        if (movieOpt.isEmpty()) {
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        Movie movie = movieOpt.get();

        boolean correct;
        switch (type) {
            case DIRECTOR -> correct = containsCsvValue(movie.getDirectorsCsv(), answerDTO.getSelectedAnswer());
            case ACTOR -> correct = containsCsvValue(movie.getActorsCsv(), answerDTO.getSelectedAnswer());
            case GENRE -> correct = containsCsvValue(movie.getGenresCsv(), answerDTO.getSelectedAnswer());
            case YEAR -> correct = String.valueOf(movie.getYear())
                    .equalsIgnoreCase(answerDTO.getSelectedAnswer().trim());
            default -> correct = false;
        }

        UUID gameId = answerDTO.getGameId();
        Optional<GameSession> sessionOpt = gameSessionRepository.findByGameId(gameId);
        if (sessionOpt.isEmpty()) {
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        GameSession session = sessionOpt.get();

        if (!correct) {
            session.setFinished(true);
            session.setFinishedAt(LocalDateTime.now());
            gameSessionRepository.save(session);
            return AnswerResultDTO.builder().correct(false).nextQuestion(null).build();
        }

        session.setScore(session.getScore() + 1);
        gameSessionRepository.save(session);

        QuestionDTO nextQuestion = generateQuestion();
        nextQuestion.setGameId(session.getGameId());
        return AnswerResultDTO.builder().correct(true).nextQuestion(nextQuestion).build();
    }

    private boolean containsCsvValue(String csv, String answer) {
        if (csv == null || csv.isBlank() || answer == null) return false;
        String[] values = csv.split(",\\s*");
        for (String val : values) {
            if (val.trim().equalsIgnoreCase(answer.trim())) return true;
        }
        return false;
    }
}
