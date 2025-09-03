package com.example.moviequizz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imdbId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String directorsCsv;

    @Column(length = 1000)
    private String actorsCsv;

    @Column(length = 1000)
    private String genresCsv;

    private Integer year;

    private Double rating;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 1000)
    private String thumbUrl;

    @Column(length = 1000)
    private String imdbUrl;
}
