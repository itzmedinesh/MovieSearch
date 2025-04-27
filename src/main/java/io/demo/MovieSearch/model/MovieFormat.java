package io.demo.MovieSearch.model;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieFormat {
    private UUID id;
    private String name;
    private String description;
}
