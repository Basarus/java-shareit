package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /users должен создавать пользователя и возвращать 201")
    void create_shouldReturnCreatedUser() throws Exception {
        UserDto input = UserDto.builder().name("user").email("user@mail.com").build();

        UserDto response = UserDto.builder().id(1L).name("user").email("user@mail.com").build();

        Mockito.when(userService.create(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(input))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("user")).andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    @DisplayName("PATCH /users/{id} должен обновлять пользователя")
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto patch = UserDto.builder().name("newName").build();

        UserDto response = UserDto.builder().id(1L).name("newName").email("user@mail.com").build();

        Mockito.when(userService.update(eq(1L), any(UserDto.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(patch))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("newName")).andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    @DisplayName("GET /users/{id} должен возвращать пользователя")
    void get_shouldReturnUser() throws Exception {
        UserDto response = UserDto.builder().id(1L).name("user").email("user@mail.com").build();

        Mockito.when(userService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("user")).andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    @DisplayName("GET /users должен возвращать список пользователей")
    void all_shouldReturnList() throws Exception {
        UserDto u1 = UserDto.builder().id(1L).name("u1").email("u1@mail.com").build();
        UserDto u2 = UserDto.builder().id(2L).name("u2").email("u2@mail.com").build();

        Mockito.when(userService.getAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("DELETE /users/{id} должен возвращать 204")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L)).andExpect(status().isNoContent());
    }
}
