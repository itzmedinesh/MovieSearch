package io.demo.MovieSearch.service;

import io.demo.MovieSearch.model.*;
import io.demo.MovieSearch.repository.MovieSearchRepository;
import io.demo.MovieSearch.service.impl.MovieSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieSearchServiceImplTest {

    @Mock
    private MovieSearchRepository movieSearchRepository;

    private MovieSearchService movieSearchService;

    private DynamoDbClient dynamoDbClient;

    @BeforeEach
    void setUp() {
        movieSearchService = new MovieSearchServiceImpl(movieSearchRepository);

        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.of("us-east-1)"))
                .build();
    }

    @Test
    void loadMovieSchedule_shouldSaveRecordToRepository() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        String city = "New York";
        Double latitude = 40.7128;
        Double longitude = -74.0060;
        String theatreName = "AMC Empire 25";

        // Act
        movieSearchService.loadMovieSchedule(scheduleId, city, latitude, longitude, theatreName, createTestMovieScheduleDetails());

        // Assert
        ArgumentCaptor<MovieScheduleGeoRecord> recordCaptor = ArgumentCaptor.forClass(MovieScheduleGeoRecord.class);
        verify(movieSearchRepository).save(recordCaptor.capture());

        MovieScheduleGeoRecord savedRecord = recordCaptor.getValue();
        assertThat(savedRecord.getCity()).isEqualTo(city);
        assertThat(savedRecord.getLatitude()).isEqualTo(latitude);
        assertThat(savedRecord.getLongitude()).isEqualTo(longitude);
        assertThat(savedRecord.getTheatreName()).isEqualTo(theatreName);
    }

    @Test
    void searchMoviesByCity_shouldCallRepositoryAndReturnResults() {
        // Arrange
        String city = "Chicago";
        List<MovieScheduleGeoRecord> expectedRecords = Arrays.asList(
                new MovieScheduleGeoRecord(UUID.randomUUID(), city, "", 41.8781, -87.6298, "AMC Empire 25", "Inception", "English", "IMAX 2D", createTestMovieScheduleDetails()),
                new MovieScheduleGeoRecord(UUID.randomUUID(), city, "", 41.8781, -87.6298, "AMC Empire 25", "Inception", "Tamil", "IMAX 2D", createTestMovieScheduleDetails())
        );
        when(movieSearchRepository.findMoviesByCity(city)).thenReturn(expectedRecords);

        // Act
        List<MovieScheduleGeoRecord> result = movieSearchService.searchMovies(city);

        // Assert
        verify(movieSearchRepository).findMoviesByCity(city);
        assertThat(result).isEqualTo(expectedRecords);
    }

    @Test
    void searchMoviesByLocation_shouldCallRepositoryWithCorrectParameters() {
        // Arrange
        String city = "Boston";
        Double latitude = 42.3601;
        Double longitude = -71.0589;
        double defaultRadius = 10.0; // Assuming default radius

        List<MovieScheduleGeoRecord> expectedRecords = Arrays.asList(
                new MovieScheduleGeoRecord(UUID.randomUUID(), city, "", latitude, longitude, "AMC Empire 25", "Inception", "English", "IMAX 3D", createTestMovieScheduleDetails())
        );
        when(movieSearchRepository.findNearByMovies(eq(city), eq(latitude), eq(longitude), anyDouble()))
                .thenReturn(expectedRecords);

        // Act
        List<MovieScheduleGeoRecord> result = movieSearchService.searchMovies(city, latitude, longitude, defaultRadius);

        // Assert
        verify(movieSearchRepository).findNearByMovies(city, latitude, longitude, defaultRadius);
        assertThat(result).isEqualTo(expectedRecords);
    }

    @Test
    void searchMoviesByTheatreName_shouldImplementCorrectLogic() {
        // Arrange
        String city = "Seattle";
        String theatreName = "Regal Meridian";
        String language = "English";
        String language1 = "Tamil";
        String format = "IMAX 3D";
        String movieName = "Inception";

        List<MovieScheduleGeoRecord> cityRecords = Arrays.asList(
                new MovieScheduleGeoRecord(UUID.randomUUID(), city, "", 47.6062, -122.3321, theatreName, movieName, language, format, createTestMovieScheduleDetails()),
                new MovieScheduleGeoRecord(UUID.randomUUID(), city, "", 47.6062, -122.3321, theatreName, movieName, language1, format, createTestMovieScheduleDetails())
        );
        when(movieSearchRepository.findMoviesByCity(city)).thenReturn(cityRecords);

        // Expected filtering behavior - only records matching the theatre name
        List<MovieScheduleGeoRecord> expectedRecords = cityRecords.stream()
                .filter(record -> theatreName.equals(record.getTheatreName()))
                .toList();

        // Act
        List<MovieScheduleGeoRecord> result = movieSearchService.searchMovies(city, theatreName);

        // Assert
        verify(movieSearchRepository).findMoviesByCity(city);
        assertThat(result).hasSize(2);
        assertThat(result.stream().filter(record -> theatreName.equals(record.getTheatreName())).count()).isEqualTo(2);
    }

    private MovieScheduleDetails createTestMovieScheduleDetails() {
        // Create a movie
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setName("Inception");
        movie.setDurationMinutes(148);
        movie.setDescription("A thief who steals corporate secrets through dream-sharing technology is given the task of planting an idea into a CEO's mind.");
        movie.setImageUrl("https://example.com/images/inception.jpg");
        movie.setTrailerUrl("https://example.com/trailers/inception.mp4");
        movie.setLanguage("English");
        movie.setFormat("IMAX 2D");

        // Add movie genres
        MovieGenre sciFi = new MovieGenre();
        sciFi.setName("Sci-Fi");
        sciFi.setDescription("Science fiction is a genre of speculative fiction which typically deals with imaginative and futuristic concepts such as advanced science and technology, space exploration, time travel, parallel universes, and extraterrestrial life.");

        MovieGenre action = new MovieGenre();
        action.setName("Action");
        action.setDescription("Action film is a film genre in which the protagonist or protagonists are thrust into a series of events that typically include violence, extended fighting, physical feats, and frantic chaos.");
        movie.setGenres(List.of(sciFi, action));

        // Add movie cast
        MovieCast cast1 = new MovieCast();
        cast1.setActorName("Leonardo DiCaprio");
        cast1.setCharacterName("Cobb");
        cast1.setRole("Protagonist");
        cast1.setProfileImageUrl("https://example.com/images/dicaprio.jpg");
        cast1.setDescription("A skilled extractor in the world of dream sharing.");

        MovieCast cast2 = new MovieCast();
        cast2.setActorName("Ellen Page");
        cast2.setCharacterName("Ariadne");
        cast2.setRole("Supporting");
        cast2.setProfileImageUrl("https://example.com/images/page.jpg");
        cast2.setDescription("A graduate student who is recruited to help create the dreams.");

        movie.setCast(List.of(cast1, cast2));

        // Create theatre
        Theatre theatre = new Theatre();
        theatre.setAddress("123 Main St, City");

        // Create screens
        Screen screen1 = new Screen();
        screen1.setId(UUID.randomUUID());
        screen1.setName("Screen 1");
        screen1.setDescription("Wide IMAX Digital Dolby");
        screen1.setCapacity(150);

        Screen screen2 = new Screen();
        screen1.setId(UUID.randomUUID());
        screen1.setName("Screen 2");
        screen1.setDescription("Wide IMAX Digital Dolby");
        screen1.setCapacity(150);


        // Create seat zone pricing
        SeatZonePricing vip = new SeatZonePricing();
        vip.setZoneName("VIP");
        vip.setCapacity(130);
        vip.setAvailableSeats(30);
        vip.setPrice(14.99);
        vip.setCurrency("USD");
        vip.setStartTime(LocalTime.of(14, 0));
        vip.setEndTime(LocalTime.of(16, 30));

        SeatZonePricing vvip = new SeatZonePricing();
        vvip.setZoneName("VVIP");
        vvip.setCapacity(20);
        vvip.setAvailableSeats(2);
        vvip.setPrice(18.99);
        vvip.setCurrency("USD");
        vvip.setStartTime(LocalTime.of(14, 0));
        vvip.setEndTime(LocalTime.of(16, 30));

        screen1.setSeatZonePricings(List.of(vip, vvip));
        screen2.setSeatZonePricings(List.of(vvip, vvip));


        // Create MovieScheduleDetails and set all components
        MovieScheduleDetails details = new MovieScheduleDetails();
        details.setTheatre(theatre);
        details.setMovie(movie);
        details.setShowDate(LocalDate.now());
        details.setScreens(List.of(screen1, screen2));
        details.setScreens(List.of(screen1, screen2));

        return details;
    }
}