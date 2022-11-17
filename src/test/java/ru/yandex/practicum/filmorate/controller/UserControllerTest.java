package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("POST create user at /users")
    public void shouldAddAndReturnUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@domen9.com\", " +
                                "\"login\": \"user-login90\", " +
                                "\"name\": \"User Name\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.email").value("user@domen9.com"))
                .andExpect(jsonPath("$.login").value("user-login90"))
                .andExpect(jsonPath("$.name").value("User Name"))
                .andExpect(jsonPath("$.birthday").value("1988-04-01"));
    }

    @Test
    @DisplayName("POST create user without name at /users")
    public void shouldAddAndReturnUser_whereLoginEqualsName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@other-domen99.com\", " +
                                "\"login\": \"user-login99\", " +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("user-login99"))
                .andExpect(jsonPath("$.name").value("user-login99"));
    }

    @Test
    @DisplayName("POST create user without email at /users")
    public void shouldReturnErrorMessageIfNoEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"user-login\", " +
                                "\"name\": \"User Name\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    @DisplayName("POST create user with invalid email at /users")
    public void shouldReturnErrorMessageIfInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user_com\", " +
                                "\"login\": \"user-login\", " +
                                "\"name\": \"User Name\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Invalid email"));
    }

    @Test
    @DisplayName("POST create user without login at /users")
    public void shouldReturnErrorMessageIfNoLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"User Name\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.login").value("Login is required"));
    }

    @Test
    @DisplayName("POST create user with invalid login at /users")
    public void shouldReturnErrorMessageIfInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user5@domen.com\", " +
                                "\"login\": \"user.login_login_login-login-login-login-login-login\", " +
                                "\"name\": \"User Name\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.login").
                        value("Login consists of letters, numbers, dash and 3-20 characters"));
    }

    @Test
    @DisplayName("POST create user with birthday in future at /users")
    public void shouldReturnErrorMessageIfInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@domen.com\", " +
                                "\"login\": \"user-login\", " +
                                "\"name\": \"User Name\"," +
                                "\"birthday\": \"2088-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.birthday").value("Birthday can't be in the future"));
    }

    @Test
    @DisplayName("POST user with already used email at /users")
    public void shouldReturnErrorMessageIfEmailInUse() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"user@domen.com\", " +
                        "\"login\": \"user-login\", " +
                        "\"name\": \"User Name\"," +
                        "\"birthday\": \"2018-04-01\"}"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@domen.com\", " +
                                "\"login\": \"user-login\", " +
                                "\"name\": \"User Name\"," +
                                " \"birthday\": \"2018-04-01\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Email already in use"));
    }
}