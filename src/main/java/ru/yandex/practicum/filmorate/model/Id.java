package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@SuperBuilder
public abstract class Id {
    @NotNull(groups = {Update.class})
    private Long id;
}