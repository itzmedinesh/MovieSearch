package io.demo.MovieSearch;

import io.demo.MovieSearch.model.*;
import io.demo.MovieSearch.repository.impl.MovieSearchRepositoryImpl;
import io.demo.MovieSearch.service.MovieSearchService;
import io.demo.MovieSearch.service.impl.MovieSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MovieSearchApplicationTests {

    private MovieSearchService movieSearchService;

    @BeforeEach
    void setUp() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.of("us-east-1"))
                .build();

        movieSearchService = new MovieSearchServiceImpl(new MovieSearchRepositoryImpl(dynamoDbClient));
    }

    @Disabled
    void loadMovieSchedule_shouldSaveAndRetrieveRecord() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        String city = "Los Angeles";
        Double latitude = 34.0522;
        Double longitude = -118.2437;
        String theatreName = "Hollywood Theatre";

        // Act
        movieSearchService.loadMovieSchedule(scheduleId, city, latitude, longitude, theatreName, createTestMovieScheduleDetails());

        // Assert
        List<MovieScheduleGeoRecord> records = movieSearchService.searchMovies(city);
        assertThat(records).hasSize(1);
        MovieScheduleGeoRecord record = records.get(0);
        System.out.println(record);
    }

    @Disabled
    void searchMoviesByCity_shouldReturnCorrectRecords() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        String city = "San Francisco";
        movieSearchService.loadMovieSchedule(scheduleId, city, 37.7749, -122.4194, "SF Theatre", new MovieScheduleDetails());

        // Act
        List<MovieScheduleGeoRecord> records = movieSearchService.searchMovies(city);

        // Assert
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getCity()).isEqualTo(city);
    }


    @Disabled
    void searchMoviesByLocation_shouldFilterByTheatreName() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        String city = "Seattle";
        //     movieSearchService.loadMovieSchedule(scheduleId, city, 47.6097, -122.3331, "Downtown Theatre", new MovieScheduleDetails());
        //   movieSearchService.loadMovieSchedule(scheduleId, city, 47.6205, -122.3493, "Space Needle Theatre", new MovieScheduleDetails());

        // Act
        List<MovieScheduleGeoRecord> records = movieSearchService.searchMovies(city, "Space Needle Theatre");
        System.out.println(records);

        // Assert
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getTheatreName()).isEqualTo("Space Needle Theatre");
    }

    @Disabled
    void searchMoviesByLocation_shouldFilterByRadius() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        String city = "Seattle";
        //      movieSearchService.loadMovieSchedule(scheduleId, city, 47.6097, -122.3331, "Downtown Theatre", new MovieScheduleDetails());
        //    movieSearchService.loadMovieSchedule(scheduleId, city, 47.6205, -122.3493, "Space Needle Theatre", new MovieScheduleDetails());

        // Act
        List<MovieScheduleGeoRecord> records = movieSearchService.searchMovies(city, 47.6097, -122.3331, 1.0);
        System.out.println(records);

        // Assert
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getTheatreName()).isEqualTo("Downtown Theatre");
    }


    @Disabled
    void searchMoviesByGenre_shouldFilterByGenre() {
        // Arrange
        UUID scheduleId1 = UUID.randomUUID();
        UUID scheduleId2 = UUID.randomUUID();
        String city = "Boston";

        // Create movie details with "Action" genre
        MovieScheduleDetails actionMovieDetails = createTestMovieScheduleDetails();
        // Make sure only Action genre is present
        MovieGenre action = new MovieGenre();
        action.setName("Action");
        action.setDescription("Action movies");
        actionMovieDetails.getMovie().setGenres(List.of(action));

        // Create movie details with "Comedy" genre
        MovieScheduleDetails comedyMovieDetails = createTestMovieScheduleDetails();
        MovieGenre comedy = new MovieGenre();
        comedy.setName("Comedy");
        comedy.setDescription("Comedy movies");
        comedyMovieDetails.getMovie().setGenres(List.of(comedy));

        // Load both movies
        movieSearchService.loadMovieSchedule(scheduleId1, city, 42.3601, -71.0589,
                "Boston Theater 1", actionMovieDetails);
        movieSearchService.loadMovieSchedule(scheduleId2, city, 42.3601, -71.0589,
                "Boston Theater 2", comedyMovieDetails);

        // Act
        List<MovieScheduleGeoRecord> actionMovies = movieSearchService.searchMoviesByGenre(city, "Action");

        // Assert
        assertThat(actionMovies).hasSize(1);
        assertThat(actionMovies.get(0).getTheatreName()).isEqualTo("Boston Theater 1");
        assertThat(actionMovies.get(0).getDetails().getMovie().getGenres())
                .extracting("name")
                .contains("Action");
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