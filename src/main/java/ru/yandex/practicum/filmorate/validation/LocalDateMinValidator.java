package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class LocalDateMinValidator implements ConstraintValidator<LocalDateMinValidation, LocalDate> {

    private LocalDate minDate;

    @Override
    public void initialize(LocalDateMinValidation annotation) {
        this.minDate = LocalDate.parse(annotation.value()).minusDays(1);
    }

    public boolean isValid(LocalDate date, ConstraintValidatorContext cxt) {
        return date == null || date.isAfter(minDate);
    }
}
