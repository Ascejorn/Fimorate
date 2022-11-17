package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Setter
@Getter
@ToString
@SuperBuilder
public class Genre extends Id {
    private String name;
}
