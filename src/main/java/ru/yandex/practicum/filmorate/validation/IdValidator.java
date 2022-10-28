package ru.yandex.practicum.filmorate.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<IdValidation, Id> {

    String className;

    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public IdValidator(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void initialize(IdValidation className) {
        this.className = className.value();
    }

    @Override
    public boolean isValid(Id o, ConstraintValidatorContext cxt) {
        cxt.buildConstraintViolationWithTemplate(cxt.getDefaultConstraintMessageTemplate())
                .addPropertyNode("id")
                .addConstraintViolation();
        switch (className) {
            case "film":
                return filmStorage.getFilmById(o.getId()).isPresent();
            case "user":
                return userStorage.getUserById(o.getId()).isPresent();
            default:
                return true;
        }
    }
}
