package io.demo.MovieSearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatZonePricing {
    private String zoneName;
    private Integer capacity;
    private Integer availableSeats;
    private Double price;
    private String currency;
    private LocalTime startTime;
    private LocalTime endTime;
}