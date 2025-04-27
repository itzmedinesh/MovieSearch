package io.demo.MovieSearch.model;

import io.demo.MovieSearch.mapper.MovieScheduleDetailsMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.UUID;

@DynamoDbBean
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieScheduleGeoRecord {
    private UUID id;
    private String city;
    private String geoHash;
    private Double latitude;
    private Double longitude;
    private String theatreName;
    private String movieName;
    private String language;
    private String format;

    private MovieScheduleDetails details;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"CityIndex"})
    public String getCity() {
        return city;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = {"TheatreIndex"})
    public String getTheatreName() {
        return theatreName;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"MovieNameIndex"})
    public String getMovieName() {
        return movieName;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"MovieLanguageIndex"})
    public String getLanguage() {
        return language;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"MovieFormatIndex"})
    public String getFormat() {
        return format;
    }

    @DynamoDbConvertedBy(MovieScheduleDetailsMapper.class)
    public MovieScheduleDetails getDetails() {
        return details;
    }
}
