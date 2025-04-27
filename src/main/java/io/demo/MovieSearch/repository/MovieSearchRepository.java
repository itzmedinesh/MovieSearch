package io.demo.MovieSearch.repository;

import io.demo.MovieSearch.model.MovieScheduleGeoRecord;

import java.util.List;

public interface MovieSearchRepository {
    void save(MovieScheduleGeoRecord record);

    List<MovieScheduleGeoRecord> findMoviesByCity(String city);

    List<MovieScheduleGeoRecord> findNearByMovies(String city, double lat, double lng, double radiusInKm);

    List<MovieScheduleGeoRecord> findMoviesByCityAndGenre(String city, String genreName);

}