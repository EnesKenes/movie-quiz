package com.example.moviequizz.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImdbMovieJsonDTO {
    private String name;
    private List<String> actors;
    private List<String> directors;
    private List<String> genre;
    private String desc;
    private String image_url;
    private String thumb_url;
    private String imdb_url; // contains IMDb ID
    private double rating;
    private int year;
}
