package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
}