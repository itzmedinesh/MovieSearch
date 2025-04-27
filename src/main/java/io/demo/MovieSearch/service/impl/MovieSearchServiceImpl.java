package io.demo.MovieSearch.service.impl;

import io.demo.MovieSearch.model.MovieScheduleDetails;
import io.demo.MovieSearch.model.MovieScheduleGeoRecord;
import io.demo.MovieSearch.repository.MovieSearchRepository;
import io.demo.MovieSearch.service.MovieSearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MovieSearchServiceImpl implements MovieSearchService {

    private final MovieSearchRepository movieSearchRepository;

    public MovieSearchServiceImpl(MovieSearchRepository movieSearchRepository) {
        this.movieSearchRepository = movieSearchRepository;
    }

    @Override
    public List<MovieScheduleGeoRecord> searchMovies(String city) {
        return movieSearchRepository.findMoviesByCity(city);
    }

    @Override
    public List<MovieScheduleGeoRecord> searchMovies(String city, String theatreName) {
        return movieSearchRepository.findMoviesByCity(city).stream().filter(record -> record.getTheatreName().equals(theatreName)).toList();
    }

    @Override
    public List<MovieScheduleGeoRecord> searchMovies(String city, Double latitude, Double longitude, Double radiusInKm) {
        return movieSearchRepository.findNearByMovies(city, latitude, longitude, radiusInKm);
    }

    @Override
    public List<MovieScheduleGeoRecord> searchMoviesByGenre(String city, String genreName) {
        return movieSearchRepository.findMoviesByCityAndGenre(city, genreName);
    }

    @Override
    public void loadMovieSchedule(UUID scheduleId, String city, Double latitude, Double longitude, String theatreName, MovieScheduleDetails movieScheduleDetails) {
        MovieScheduleGeoRecord record = new MovieScheduleGeoRecord();
        record.setId(scheduleId);
        record.setCity(city);
        record.setGeoHash(generateSimpleGeoHash(latitude, longitude));
        record.setLatitude(latitude);
        record.setLongitude(longitude);
        record.setTheatreName(theatreName);
        record.setMovieName(movieScheduleDetails.getMovie().getName());
        record.setLanguage(movieScheduleDetails.getMovie().getLanguage());
        record.setFormat(movieScheduleDetails.getMovie().getFormat());
        record.setDetails(movieScheduleDetails);
        movieSearchRepository.save(record);

    }
}
