package ru.yandex.practicum.filmorate.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsedEmailValidator implements ConstraintValidator<UsedEmailValidation, String> {

    private final UserStorage userStorage;

    @Autowired
    public UsedEmailValidator(UserStorage storage) {
        this.userStorage = storage;
    }

    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        return userStorage.getAllUsers().stream().noneMatch(u -> u.getEmail().equals(email));
    }
}
