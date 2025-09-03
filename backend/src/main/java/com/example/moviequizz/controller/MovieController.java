package com.example.moviequizz.controller;

import com.example.moviequizz.dto.ImdbMovieJsonDTO;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.service.OmdbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Movie Management API", description = "Endpoint for importing top movies from OMDB")
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

    @Operation(
            summary = "Import IMDb Top Movies",
            description = "Imports movies from a local `top250_min.json` file and enriches them with details from the OMDb API. "
                    + "Movies are saved into the database only if they don't already exist. "
                    + "This operation is **restricted to admin users only**.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movies imported successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file or parsing error",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected server error",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
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

