package io.demo.MovieSearch.repository.impl;

import io.demo.MovieSearch.model.MovieScheduleGeoRecord;
import io.demo.MovieSearch.repository.MovieSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class MovieSearchRepositoryImpl extends AbstractMovieSearchRepository implements MovieSearchRepository {

    private static final String TABLE_NAME = "MovieScheduleGeoRecord";

    @Autowired
    public MovieSearchRepositoryImpl(DynamoDbClient dynamoDbClient) {
        super(dynamoDbClient, TABLE_NAME);
    }

    @Override
    public void save(MovieScheduleGeoRecord record) {
        log.info("Saving record: " + record);
        table.putItem(record);
    }

    @Override
    public List<MovieScheduleGeoRecord> findMoviesByCity(String city) {
        log.info("Searching movies by city: " + city);
        return table.index("CityIndex")
                .query(q -> q.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder().partitionValue(city).build())))
                .stream().map(Page::items).collect(Collectors.toSet()).stream().flatMap(List::stream).toList();
    }

    @Override
    public List<MovieScheduleGeoRecord> findNearByMovies(String city, double lat, double lng, double radiusInKm) {

        log.info("Searching movies by city: " + city + " and radius: " + radiusInKm);
        List<MovieScheduleGeoRecord> all = findMoviesByCity(city);

        return all.stream()
                .filter(record -> {
                    double distance = haversineDistance(lat, lng, record.getLatitude(), record.getLongitude());
                    log.info("Distance between " + lat + "," + lng + " and " + record.getLatitude() + "," + record.getLongitude() + " is " + distance);
                    return distance <= radiusInKm;
                })
                .toList();
    }

    @Override
    public List<MovieScheduleGeoRecord> findMoviesByCityAndGenre(String city, String genreName) {

        log.info("Searching movies by city: " + city + " and genre: " + genreName);
        // First get all movies in the city
        List<MovieScheduleGeoRecord> cityMovies = findMoviesByCity(city);

        // Then filter by genre using Java Stream API
        return cityMovies.stream()
                .filter(record -> {
                    if (record.getDetails() != null
                            && record.getDetails().getMovie() != null
                            && record.getDetails().getMovie().getGenres() != null) {

                        return record.getDetails().getMovie().getGenres().stream()
                                .anyMatch(genre -> genreName.equalsIgnoreCase(genre.getName()));
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

}
