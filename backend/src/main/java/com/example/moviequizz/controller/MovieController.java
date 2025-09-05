package com.example.moviequizz.controller;

import com.example.moviequizz.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movie Management API", description = "Endpoint for importing top movies from OMDB")
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(
            summary = "Import IMDb Top Movies",
            description = "Imports movies from a local `top250_min.json` file and enriches them with details from the OMDb API. Movies are saved into the database only if they don't already exist. This operation is **restricted to admin users only**.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movies imported successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file or parsing error"),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error")
            }
    )
    @PostMapping("/import-top")
    public String importTopMovies() {
        return movieService.importTopMovies();
    }
}
