package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IdValidator.class)
public @interface IdValidation {
    String message() default "Неправильный ID или нет {value} с этим ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String value();
}
