package com.example.moviequizz.service.impl;

import com.example.moviequizz.dto.ImdbMovieJsonDTO;
import com.example.moviequizz.entity.Movie;
import com.example.moviequizz.repository.MovieRepository;
import com.example.moviequizz.service.MovieService;
import com.example.moviequizz.service.OmdbService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieServiceImpl implements MovieService {

    private final OmdbService omdbService;
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public MovieServiceImpl(
            OmdbService omdbService, MovieRepository movieRepository, ObjectMapper objectMapper) {
        this.omdbService = omdbService;
        this.movieRepository = movieRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public String importTopMovies() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/top250_min.json");
            if (inputStream == null) throw new IllegalArgumentException("File not found!");

            List<ImdbMovieJsonDTO> movieList =
                    objectMapper.readValue(inputStream, new TypeReference<List<ImdbMovieJsonDTO>>() {});

            for (ImdbMovieJsonDTO jsonMovie : movieList) {
                String imdbId = jsonMovie.getImdb_url().replace("/title/", "").replace("/", "");

                if (movieRepository.existsByImdbId(imdbId)) continue;

                Map<String, Object> omdbData = omdbService.getMovieByImdbId(imdbId);
                if (omdbData == null
                        || omdbData.get("Title") == null
                        || omdbData.get("Director") == null) continue;

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
