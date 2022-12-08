package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
public class Film extends Id {
    @NotBlank(message = "Name should not be blank.", groups = {Create.class})
    private String name;

    @NotNull(message = "Description is required.", groups = {Create.class})
    @Size(
            max = 200,
            message = "Description should be less than {max} characters.",
            groups = {Create.class, Update.class}
    )
    private String description;

    @NotNull(message = "Release date is required.", groups = {Create.class})
    @LocalDateMinValidation(value = "1895-12-28", groups = {Create.class, Update.class})
    private LocalDate releaseDate;

    @NotNull(message = "Duration is required.", groups = {Create.class})
    @Positive(message = "Duration should be positive.", groups = {Create.class, Update.class})
    private Long duration;

    @NotNull(message = "MPA rating is required.", groups = {Create.class})
    private Mpa mpa;

    private List<Genre> genres;

    private List<Director> directors;
}