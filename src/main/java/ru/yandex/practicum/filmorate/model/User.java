package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validation.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends Id {
    @NotNull(message = "Email is required.", groups = {Create.class})
    @Email(message = "Invalid email.", groups = {Create.class, Update.class})
    @UsedEmailValidation(groups = {Create.class})
    private String email;

    @NotBlank(message = "Login is required.", groups = {Create.class})
    @Pattern(
            regexp = "^(?=.{3,20}$)(?!-)[a-zA-Z0-9-]+(?<!-)$",
            message = "Login consists of letters, numbers, dash and 3-20 characters.",
            groups = {Create.class, Update.class}
    )
    @UsedLoginValidation(groups = {Create.class})
    private String login;

    private String name;

    @Past(message = "Birthday can't be in the future.", groups = {Create.class, Update.class})
    private LocalDate birthday;
}
