package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Create;

import javax.validation.constraints.Max;

@Data
public abstract class Id {
    @Max(value = 0, message = "У нового оюъекта не должно быть id.", groups = {Create.class})
    long id;
}
