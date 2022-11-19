package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
@ToString
@SuperBuilder
public class Genre extends Id {
    @NotNull
    private String name;
}
