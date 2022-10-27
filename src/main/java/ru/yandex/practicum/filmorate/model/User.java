package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.IdValidation;
import ru.yandex.practicum.filmorate.validation.Update;
import ru.yandex.practicum.filmorate.validation.UsedEmailValidation;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
@IdValidation(value = "user", groups = {Update.class})
public class User extends Id {
    @NotNull(message = "Email не должно быть пустым.", groups = {Create.class})
    @Email(message = "Неправильный email", groups = {Create.class, Update.class})
    @UsedEmailValidation(groups = {Create.class, Update.class})
    private String email;

    @NotBlank(message = "Login не должен быть пустым.", groups = {Create.class})
    @Pattern(
            regexp = "^(?=.{3,20}$)(?![-])[a-zA-Z0-9-]+(?<![-])$",
            message = "Login должен сосотоять из букв, цифр и быть от 3 до 20 символов.",
            groups = {Create.class, Update.class}
    )
    private String login;

    private String name;

    @Past(message = "День рождения не должен быть в будущем.", groups = {Create.class, Update.class})
    private LocalDate birthday;
}
