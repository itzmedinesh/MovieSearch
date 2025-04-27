package io.demo.MovieSearch.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCast {
    private String actorName;
    private String characterName;
    private String role;
    private String profileImageUrl;
    private String description;
}
