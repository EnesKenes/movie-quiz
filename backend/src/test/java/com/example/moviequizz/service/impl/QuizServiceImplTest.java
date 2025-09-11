package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.*;
import com.example.moviequizz.entity.GameSession;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.GameSessionRepository;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private QuizServiceImpl quizService;

    private List<Movie> movies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create 4 movies to avoid generateRandomOptions infinite loop
        Movie movie1 = Movie.builder()
                .imdbId("tt1111111")
                .title("Movie 1")
                .directorsCsv("Director A, Director B")
                .actorsCsv("Actor A, Actor B")
                .genresCsv("Comedy, Drama")
                .year(2020)
                .imageUrl("http://example.com/image1.jpg")
                .build();

        Movie movie2 = Movie.builder()
                .imdbId("tt2222222")
                .title("Movie 2")
                .directorsCsv("Director C")
                .actorsCsv("Actor C")
                .genresCsv("Action")
                .year(2019)
                .imageUrl("http://example.com/image2.jpg")
                .build();

        Movie movie3 = Movie.builder()
                .imdbId("tt3333333")
                .title("Movie 3")
                .directorsCsv("Director D")
                .actorsCsv("Actor D")
                .genresCsv("Horror")
                .year(2018)
                .imageUrl("http://example.com/image3.jpg")
                .build();

        Movie movie4 = Movie.builder()
                .imdbId("tt4444444")
                .title("Movie 4")
                .directorsCsv("Director E")
                .actorsCsv("Actor E")
                .genresCsv("Thriller")
                .year(2021)
                .imageUrl("http://example.com/image4.jpg")
                .build();

        movies = List.of(movie1, movie2, movie3, movie4);

        // Use deterministic random for test stability
        quizService = new QuizServiceImpl(movieRepository, gameSessionRepository, jwtUtil) {
            protected Random getRandom() {
                return new Random(0); // deterministic
            }
        };
    }

    @Test
    void testStartNewGame_createsSessionAndReturnsFirstQuestion() {
        when(movieRepository.findAll()).thenReturn(movies);
        when(jwtUtil.generateQuestionToken(any(), any(), anyLong())).thenReturn("token123");

        QuestionDTO question = quizService.startNewGame("testUser");

        assertNotNull(question);
        assertEquals("token123", question.getToken());
        assertNotNull(question.getQuestionText());

        ArgumentCaptor<GameSession> captor = ArgumentCaptor.forClass(GameSession.class);
        verify(gameSessionRepository).save(captor.capture());
        assertEquals("testUser", captor.getValue().getUsername());
    }

    @Test
    void testGenerateQuestion_returnsQuestionOfSpecifiedType() {
        when(movieRepository.findAll()).thenReturn(movies);
        when(jwtUtil.generateQuestionToken(any(), any(), anyLong())).thenReturn("token123");

        QuestionDTO question = quizService.generateQuestion(QuestionType.DIRECTOR);

        assertNotNull(question);
        assertEquals(QuestionType.DIRECTOR, question.getType());
        assertTrue(question.getOptions().contains("Director A") || question.getOptions().contains("Director B"));
    }

    @Test
    void testSubmitAnswer_correctAnswer_increasesScore() {
        UUID gameId = UUID.randomUUID();
        GameSession session = GameSession.builder()
                .gameId(gameId)
                .username("user")
                .score(0)
                .finished(false)
                .build();

        AnswerDTO answerDTO = AnswerDTO.builder()
                .gameId(gameId)
                .selectedAnswer("Director A")
                .token("token123")
                .build();

        when(jwtUtil.extractQuestionId("token123")).thenReturn("tt1111111");
        when(jwtUtil.isTokenExpired("token123")).thenReturn(false);
        when(jwtUtil.extractQuestionType("token123")).thenReturn(QuestionType.DIRECTOR);
        when(movieRepository.findByImdbId("tt1111111")).thenReturn(Optional.of(movies.get(0)));
        when(gameSessionRepository.findByGameId(gameId)).thenReturn(Optional.of(session));
        when(movieRepository.findAll()).thenReturn(movies);
        when(jwtUtil.generateQuestionToken(any(), any(), anyLong())).thenReturn("tokenNext");

        AnswerResultDTO result = quizService.submitAnswer(answerDTO);

        assertTrue(result.isCorrect());
        assertEquals(1, session.getScore());
        assertNotNull(result.getNextQuestion());
        assertEquals(gameId, result.getNextQuestion().getGameId());
    }

    @Test
    void testSubmitAnswer_incorrectAnswer_finishesGame() {
        UUID gameId = UUID.randomUUID();
        GameSession session = GameSession.builder()
                .gameId(gameId)
                .username("user")
                .score(0)
                .finished(false)
                .build();

        AnswerDTO answerDTO = AnswerDTO.builder()
                .gameId(gameId)
                .selectedAnswer("Wrong Answer")
                .token("token123")
                .build();

        when(jwtUtil.extractQuestionId("token123")).thenReturn("tt1111111");
        when(jwtUtil.isTokenExpired("token123")).thenReturn(false);
        when(jwtUtil.extractQuestionType("token123")).thenReturn(QuestionType.DIRECTOR);
        when(movieRepository.findByImdbId("tt1111111")).thenReturn(Optional.of(movies.get(0)));
        when(gameSessionRepository.findByGameId(gameId)).thenReturn(Optional.of(session));

        AnswerResultDTO result = quizService.submitAnswer(answerDTO);

        assertFalse(result.isCorrect());
        assertTrue(session.isFinished());
        assertNotNull(session.getFinishedAt());
        assertNull(result.getNextQuestion());
    }
}
