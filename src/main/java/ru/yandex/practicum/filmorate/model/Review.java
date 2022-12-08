package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class Review {

    @NotNull(message = "Review id is required.", groups = {Update.class})
    private Long reviewId;

    @NotNull(message = "Film id is required.", groups = {Create.class})
    private Long filmId;

    @NotNull(message = "User id is required.", groups = {Create.class})
    private Long userId;

    @NotNull(message = "Field 'is positive' is required.", groups = {Create.class})
    private Boolean isPositive;

    @NotNull(message = "Content is required.", groups = {Create.class})
    private String content;

    private int useful;
}
