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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class FilmControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("POST create film at /films")
    public void shouldAddAndReturnFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.name").value("Monty Python and the Holy Grail"))
                .andExpect(jsonPath("$.description").value("King Arthur search for the Holy Grail"))
                .andExpect(jsonPath("$.releaseDate").value("1975-03-14"))
                .andExpect(jsonPath("$.duration").value(91));
    }

    @Test
    @DisplayName("POST create film without \"name\" field at /films")
    public void shouldReturnErrorMessageIfNoName() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Name should be not blank"));
    }

    @Test
    @DisplayName("POST create film with blank \"name\" field at /films")
    public void shouldReturnErrorMessageIfBlankName() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"   \", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Name should be not blank"));
    }

    @Test
    @DisplayName("POST create film without \"description\" field at /films")
    public void shouldReturnErrorMessageIfNoDescription() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description").value("Description is required"));
    }

    @Test
    @DisplayName("POST create film with \"description\"s length equals 201 at /films")
    public void shouldReturnErrorMessageIfDescriptionLengthIs201() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"Lorem ipsum dolor sit amet, " +
                                "consectetur adipiscing elit, sed do eiusmod tempor " +
                                "incididunt ut labore et dolore magna aliqua. Ut enim " +
                                "ad minim veniam, quis nostrud exercitation ullamco " +
                                "laboris nisi ut al\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description")
                        .value("Description should be less 200 characters"));
    }

    @Test
    @DisplayName("POST create film with \"description\"s length equals 200 at /films")
    public void shouldAddAndReturnFilmIfDescriptionLengthIs200() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"Lorem ipsum dolor sit amet, " +
                                "consectetur adipiscing elit, sed do eiusmod tempor " +
                                "incididunt ut labore et dolore magna aliqua. Ut enim " +
                                "ad minim veniam, quis nostrud exercitation ullamco " +
                                "laboris nisi ut a\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST create film without \"releaseDate\" field at /films")
    public void shouldReturnErrorMessageIfNoReleaseDate() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.releaseDate").value("Release is required"));
    }

    @Test
    @DisplayName("POST create film with \"releaseDate\" before 1895-12-28 at /films")
    public void shouldReturnErrorMessageIfReleaseDateBefore1895_12_28() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1895-12-27\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.releaseDate")
                        .value("Movie should be released after 1895-12-28"));
    }

    @Test
    @DisplayName("POST create film with \"releaseDate\" equals 1895-12-28 at /films")
    public void shouldReturnErrorMessageIfReleaseDateEquals1895_12_28() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1895-12-28\"," +
                                "\"duration\": 91," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST create film without \"duration\" field at /films")
    public void shouldReturnErrorMessageIfNoDuration() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.duration")
                        .value("Duration should be positive"));
    }

    @Test
    @DisplayName("POST create film with negative \"duration\" field at /films")
    public void shouldReturnErrorMessageIfNegativeDuration() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Monty Python and the Holy Grail\", " +
                                "\"description\": \"King Arthur search for the Holy Grail\", " +
                                "\"releaseDate\": \"1975-03-14\", " +
                                "\"duration\": -1," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.duration").value("Duration should be positive"));
    }

    @Test
    @DisplayName("PUT update film at /films")
    public void shouldUpdateAndReturnFilm() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"name\": \"Updated name\", " +
                                "\"description\": \"Updated description\", " +
                                "\"releaseDate\": \"2000-03-14\","  +
                                "\"duration\": 191," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-03-14"))
                .andExpect(jsonPath("$.duration").value(191));
    }
}