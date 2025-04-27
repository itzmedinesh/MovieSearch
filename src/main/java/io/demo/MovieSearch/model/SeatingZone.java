package io.demo.MovieSearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatingZone {
    private String name;
    private String description;
    private List<Seat> seats;
}
