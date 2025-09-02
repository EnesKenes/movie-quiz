package com.example.moviequizz.controller;

import com.example.moviequizz.dto.ImdbMovieJsonDTO;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.service.OmdbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final OmdbService omdbService;
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public MovieController(OmdbService omdbService, MovieRepository movieRepository, ObjectMapper objectMapper) {
        this.omdbService = omdbService;
        this.movieRepository = movieRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Bulk populate movies from a JSON file.
     */
    @PostMapping("/import-top")
    public String importTopMovies() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/top250_min.json");
            if (inputStream == null) throw new IllegalArgumentException("File not found!");

            List<ImdbMovieJsonDTO> movieList = objectMapper.readValue(
                    inputStream,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ImdbMovieJsonDTO>>() {}
            );

            for (ImdbMovieJsonDTO jsonMovie : movieList) {
                String imdbId = jsonMovie.getImdb_url().replace("/title/", "").replace("/", "");

                if (movieRepository.existsByImdbId(imdbId)) continue;

                Map<String, Object> omdbData = omdbService.getMovieByImdbId(imdbId);
                if (omdbData == null || omdbData.get("Title") == null || omdbData.get("Director") == null)
                    continue;

                Movie movie = Movie.builder()
                        .imdbId(imdbId)
                        .title((String) omdbData.get("Title"))
                        .directorsCsv(safeString(omdbData.get("Director")))
                        .actorsCsv(safeString(omdbData.get("Actors")))
                        .genresCsv(safeString(omdbData.get("Genre")))
                        .year(omdbData.get("Year") != null ? Integer.parseInt((String) omdbData.get("Year")) : null)
                        .rating(omdbData.get("imdbRating") != null ? Double.parseDouble((String) omdbData.get("imdbRating")) : null)
                        .description((String) omdbData.get("Plot"))
                        .imageUrl((String) omdbData.get("Poster"))
                        .thumbUrl((String) omdbData.get("Poster"))
                        .imdbUrl("https://www.imdb.com/title/" + imdbId)
                        .build();

                movieRepository.save(movie);
            }

            return "Top movies imported successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error importing movies: " + e.getMessage();
        }
    }

    /** Helper method: safely convert OMDB field to string */
    private String safeString(Object field) {
        if (field == null) return "";
        String s = ((String) field).trim();
        return (s.isBlank() || s.equalsIgnoreCase("N/A")) ? "" : s;
    }


}

