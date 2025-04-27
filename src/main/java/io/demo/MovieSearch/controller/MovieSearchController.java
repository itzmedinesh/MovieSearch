package io.demo.MovieSearch.controller;

import io.demo.MovieSearch.model.MovieScheduleDetails;
import io.demo.MovieSearch.model.MovieScheduleGeoRecord;
import io.demo.MovieSearch.service.MovieSearchService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieSearchController {

    private final MovieSearchService movieSearchService;

    @Autowired
    public MovieSearchController(MovieSearchService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @GetMapping
    public ResponseEntity<List<MovieScheduleGeoRecord>> getMoviesByCity(
            @RequestParam String city) {
        List<MovieScheduleGeoRecord> movies = movieSearchService.searchMovies(city);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/theatres")
    public ResponseEntity<List<MovieScheduleGeoRecord>> getMoviesByTheatre(
            @RequestParam String city,
            @RequestParam String theatreName) {
        List<MovieScheduleGeoRecord> movies = movieSearchService.searchMovies(city, theatreName);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<MovieScheduleGeoRecord>> getNearbyMovies(
            @RequestParam String city,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius) {
        List<MovieScheduleGeoRecord> movies = movieSearchService.searchMovies(
                city, latitude, longitude, radius);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<MovieScheduleGeoRecord>> getMoviesByGenre(
            @RequestParam String city,
            @RequestParam String genre) {
        List<MovieScheduleGeoRecord> movies = movieSearchService.searchMoviesByGenre(city, genre);
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/schedules")
    public ResponseEntity<Void> createMovieSchedule(@RequestBody ScheduleRequest request) {
        movieSearchService.loadMovieSchedule(
                request.getScheduleId(),
                request.getCity(),
                request.getLatitude(),
                request.getLongitude(),
                request.getTheatreName(),
                request.getDetails()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class ScheduleRequest {
        private UUID scheduleId;
        private String city;
        private Double latitude;
        private Double longitude;
        private String theatreName;
        private MovieScheduleDetails details;
    }
}
