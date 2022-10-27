package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.Create;

import javax.validation.constraints.Max;

@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@SuperBuilder
public abstract class Id {
    @Max(value = 0, message = "У нового оюъекта не должно быть id.", groups = {Create.class})
    private long id;
}
