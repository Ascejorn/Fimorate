package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;

    @Test
    void createUserOrdinaryEmailTest() {
        User testUser = new User(1,"aaa@mail.ru","alexLoLo","alex", LocalDate.of(1999,01,30));
        userController.createUser(testUser);
        assertTrue(userController.getUsers().contains(testUser));
        assertEquals(testUser,userController.getUsers().get(0));
    }

    @Test
    void createUserWithBlankEmailTest() {
        User testUser = new User(1,"","alexLoLo","alex", LocalDate.of(1999,01,30));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser));
        assertEquals(exception.getMessage(),"Электронная почта не может быть пустой и должна содержать символ @ .");
    }

    @Test
    void createUserWithEmailWithoutSymbolTest() {
        User testUser = new User(1,"invalid.email.ru","alexLoLo","alex", LocalDate.of(1999,01,30));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser));
        assertEquals(exception.getMessage(),"Электронная почта не может быть пустой и должна содержать символ @ .");
    }



    @Test
    void createUserWithBlankLoginTest() {
        User testUser = new User(1,"valid@email.ru","","alex", LocalDate.of(1999,01,30));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser));
        assertEquals(exception.getMessage(),"Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void createUserWithLoginWithBlanksTest() {
        User testUser = new User(1,"valid@email.ru","a a a","alex", LocalDate.of(1999,01,30));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser));
        assertEquals(exception.getMessage(),"Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void createUserWithBlankNameTest() {
        User testUser = new User(1,"valid@email.ru","aaaa","", LocalDate.of(1999,01,30));
        userController.createUser(testUser);
        assertTrue(userController.getUsers().contains(testUser));
        assertEquals(testUser,userController.getUsers().get(0));
        assertEquals(testUser.getName(),userController.getUsers().get(0).getLogin());
    }

    @Test
    void createUserWithDateOfBirthInFutureTest() {
        User testUser = new User(1,"valid@email.ru","alexlolo","alex", LocalDate.of(2024,01,30));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser));
        assertEquals(exception.getMessage(),"Дата рождения не может быть в будущем.");
    }
}