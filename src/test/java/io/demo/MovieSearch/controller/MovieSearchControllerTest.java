package io.demo.MovieSearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.demo.MovieSearch.model.Movie;
import io.demo.MovieSearch.model.MovieGenre;
import io.demo.MovieSearch.model.MovieScheduleDetails;
import io.demo.MovieSearch.model.MovieScheduleGeoRecord;
import io.demo.MovieSearch.service.MovieSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieSearchController.class)
public class MovieSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieSearchService movieSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieScheduleGeoRecord movieRecord;
    private MovieSearchController.ScheduleRequest scheduleRequest;

    @BeforeEach
    void setUp() {
        // Setup test movie record
        movieRecord = new MovieScheduleGeoRecord();
        movieRecord.setId(UUID.randomUUID());
        movieRecord.setCity("New York");
        movieRecord.setLatitude(40.7128);
        movieRecord.setLongitude(-74.0060);
        movieRecord.setTheatreName("AMC Theatre");

        MovieScheduleDetails details = new MovieScheduleDetails();
        Movie movie = new Movie();
        movie.setName("Test Movie");

        MovieGenre genre = new MovieGenre();
        genre.setName("Action");
        genre.setDescription("Action movies");
        movie.setGenres(Collections.singletonList(genre));

        details.setMovie(movie);
        movieRecord.setDetails(details);

        // Setup schedule request
        scheduleRequest = new MovieSearchController.ScheduleRequest();
        scheduleRequest.setScheduleId(UUID.randomUUID());
        scheduleRequest.setCity("New York");
        scheduleRequest.setLatitude(40.7128);
        scheduleRequest.setLongitude(-74.0060);
        scheduleRequest.setTheatreName("AMC Theatre");
        scheduleRequest.setDetails(details);
    }

    @Test
    void getMoviesByCity_ShouldReturnMoviesList() throws Exception {
        // Arrange
        String city = "New York";
        List<MovieScheduleGeoRecord> movies = Collections.singletonList(movieRecord);
        when(movieSearchService.searchMovies(city)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/api/movies")
                        .param("city", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].city", is("New York")))
                .andExpect(jsonPath("$[0].theatreName", is("AMC Theatre")))
                .andExpect(jsonPath("$[0].details.movie.name", is("Test Movie")));

        verify(movieSearchService).searchMovies(city);
    }

    @Test
    void getMoviesByTheatre_ShouldReturnFilteredMovies() throws Exception {
        // Arrange
        String city = "New York";
        String theatre = "AMC Theatre";
        List<MovieScheduleGeoRecord> movies = Collections.singletonList(movieRecord);
        when(movieSearchService.searchMovies(city, theatre)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/api/movies/theatres")
                        .param("city", city)
                        .param("theatreName", theatre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].city", is("New York")))
                .andExpect(jsonPath("$[0].theatreName", is("AMC Theatre")));

        verify(movieSearchService).searchMovies(city, theatre);
    }

    @Test
    void getNearbyMovies_ShouldReturnNearbyMovies() throws Exception {
        // Arrange
        String city = "New York";
        Double latitude = 40.7128;
        Double longitude = -74.0060;
        Double radius = 10.0;
        List<MovieScheduleGeoRecord> movies = Collections.singletonList(movieRecord);
        when(movieSearchService.searchMovies(city, latitude, longitude, radius)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/api/movies/nearby")
                        .param("city", city)
                        .param("latitude", latitude.toString())
                        .param("longitude", longitude.toString())
                        .param("radius", radius.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].latitude", is(latitude)))
                .andExpect(jsonPath("$[0].longitude", is(longitude)));

        verify(movieSearchService).searchMovies(city, latitude, longitude, radius);
    }

    @Test
    void getMoviesByGenre_ShouldReturnMoviesFilteredByGenre() throws Exception {
        // Arrange
        String city = "New York";
        String genre = "Action";
        List<MovieScheduleGeoRecord> movies = Collections.singletonList(movieRecord);
        when(movieSearchService.searchMoviesByGenre(city, genre)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/api/movies/genres")
                        .param("city", city)
                        .param("genre", genre)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].details.movie.genres[0].name", is("Action")));

        verify(movieSearchService).searchMoviesByGenre(city, genre);
    }

    @Test
    void createMovieSchedule_ShouldReturnCreatedStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/movies/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest)))
                .andExpect(status().isCreated());

        verify(movieSearchService).loadMovieSchedule(
                any(UUID.class),
                eq(scheduleRequest.getCity()),
                eq(scheduleRequest.getLatitude()),
                eq(scheduleRequest.getLongitude()),
                eq(scheduleRequest.getTheatreName()),
                eq(scheduleRequest.getDetails()));
    }
}