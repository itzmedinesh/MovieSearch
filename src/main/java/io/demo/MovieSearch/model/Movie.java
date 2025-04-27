package io.demo.MovieSearch.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private UUID id;
    private String name;
    private Integer durationMinutes;
    private String description;
    private String imageUrl;
    private String trailerUrl;
    private String language;
    private String format;
    private List<MovieGenre> genres;
    private List<MovieCast> cast;
}

