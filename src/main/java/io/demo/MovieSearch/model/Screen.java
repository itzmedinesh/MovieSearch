package io.demo.MovieSearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screen {
    private UUID id;
    private String name;
    private String description;
    private Integer capacity;
    private List<SeatZonePricing> seatZonePricings;
}
