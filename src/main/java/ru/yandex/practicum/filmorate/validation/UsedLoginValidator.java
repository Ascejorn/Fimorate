package ru.yandex.practicum.filmorate.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsedLoginValidator implements ConstraintValidator<UsedLoginValidation, String> {

    private final UserService service;

    @Autowired
    public UsedLoginValidator(UserService userService) {
        this.service = userService;
    }

    public boolean isValid(String login, ConstraintValidatorContext cxt) {
        return service.isNotExistLogin(login);
    }
}
