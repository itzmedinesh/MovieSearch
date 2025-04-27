package io.demo.MovieSearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieScheduleDetails {
    private Theatre theatre;
    private Movie movie;
    private LocalDate showDate;
    private List<Screen> screens;
}