package com.example.moviequizz.service;

import java.util.Map;

public interface OmdbService {

    Map<String, Object> getMovieByTitle(String title);

    Map<String, Object> getMovieByImdbId(String imdbId);
}
