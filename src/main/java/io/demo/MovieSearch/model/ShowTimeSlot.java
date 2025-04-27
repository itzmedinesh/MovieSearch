package io.demo.MovieSearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeSlot {
    private UUID slotId;
    private LocalTime startTime;
    private LocalTime endTime;
}