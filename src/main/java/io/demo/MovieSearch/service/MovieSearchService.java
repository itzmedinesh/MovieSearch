package io.demo.MovieSearch.service;

import io.demo.MovieSearch.model.MovieScheduleDetails;
import io.demo.MovieSearch.model.MovieScheduleGeoRecord;

import java.util.List;
import java.util.UUID;

public interface MovieSearchService {

    List<MovieScheduleGeoRecord> searchMovies(String city);

    List<MovieScheduleGeoRecord> searchMovies(String city, String theatreName);

    List<MovieScheduleGeoRecord> searchMovies(String city, Double latitude, Double longitude, Double radiusInKm);

    List<MovieScheduleGeoRecord> searchMoviesByGenre(String city, String genreName);

    void loadMovieSchedule(UUID scheduleId, String city, Double latitude, Double longitude, String theatreName, MovieScheduleDetails movieScheduleDetails);

    default String generateSimpleGeoHash(double lat, double lon) {
        return String.format("%.4f#%.4f", lat, lon);
    }
}
