package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@SuperBuilder
public abstract class Id {
    private long id;
}