package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    @DisplayName("POST /items должен создавать вещь и возвращать 201")
    void create_shouldReturnCreatedItem() throws Exception {
        ItemDto input = ItemDto.builder().name("Дрель").description("Аккумуляторная").available(true).build();

        ItemDto responseBody = ItemDto.builder().id(1L).name("Дрель").description("Аккумуляторная").available(true).build();

        Mockito.when(itemClient.create(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1L).content(mapper.writeValueAsString(input))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("Дрель")).andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("PATCH /items/{id} должен обновлять вещь")
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto patch = ItemDto.builder().name("Новая дрель").build();

        ItemDto responseBody = ItemDto.builder().id(1L).name("Новая дрель").description("Аккумуляторная").available(true).build();

        Mockito.when(itemClient.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(patch("/items/{itemId}", 1L).contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1L).content(mapper.writeValueAsString(patch))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("Новая дрель"));
    }

    @Test
    @DisplayName("GET /items/{id} должен возвращать вещь")
    void get_shouldReturnItem() throws Exception {
        ItemDto dto = ItemDto.builder().id(1L).name("Дрель").description("Аккумуляторная").available(true).build();

        Mockito.when(itemClient.get(1L, 1L)).thenReturn(ResponseEntity.ok(dto));

        mockMvc.perform(get("/items/{itemId}", 1L).header("X-Sharer-User-Id", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    @DisplayName("GET /items должен возвращать список вещей владельца")
    void ownerItems_shouldReturnList() throws Exception {
        ItemDto i1 = ItemDto.builder().id(1L).name("Дрель").description("Аккум").available(true).build();
        ItemDto i2 = ItemDto.builder().id(2L).name("Отвертка").description("Крестовая").available(true).build();

        List<ItemDto> responseBody = List.of(i1, i2);

        Mockito.when(itemClient.getByOwner(1L)).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /items/search должен искать вещи")
    void search_shouldReturnList() throws Exception {
        ItemDto i1 = ItemDto.builder().id(1L).name("Дрель").description("Аккум").available(true).build();

        List<ItemDto> responseBody = List.of(i1);

        Mockito.when(itemClient.search(1L, "дрель")).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", 1L).param("text", "дрель")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    @DisplayName("POST /items/{id}/comment должен добавлять комментарий")
    void addComment_shouldReturnComment() throws Exception {
        CommentDto input = CommentDto.builder().text("Отличная вещь!").build();

        CommentDto responseBody = CommentDto.builder().id(1L).text("Отличная вещь!").authorName("user").created(LocalDateTime.now()).build();

        Mockito.when(itemClient.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(post("/items/{itemId}/comment", 1L).header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.text").value("Отличная вещь!")).andExpect(jsonPath("$.authorName").value("user"));
    }
}
