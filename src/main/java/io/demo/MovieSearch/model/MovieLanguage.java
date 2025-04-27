package io.demo.MovieSearch.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieLanguage {
    private UUID id;
    private String name;
    private String description;
}
