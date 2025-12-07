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
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    @DisplayName("POST /requests должен возвращать созданный запрос")
    void create_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto input = ItemRequestDto.builder().description("Нужен шуруповерт").build();

        ItemRequestDto responseBody = ItemRequestDto.builder().id(1L).description("Нужен шуруповерт").created(LocalDateTime.now()).items(List.of()).build();

        Mockito.when(itemRequestClient.create(Mockito.eq(1L), Mockito.any(ItemRequestDto.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(responseBody));

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1L).content(mapper.writeValueAsString(input))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.description").value("Нужен шуруповерт"));
    }

    @Test
    @DisplayName("GET /requests должен возвращать список собственных запросов")
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestDto r1 = ItemRequestDto.builder().id(1L).description("Нужен велосипед").created(LocalDateTime.now()).items(List.of()).build();

        ItemRequestDto r2 = ItemRequestDto.builder().id(2L).description("Нужна удочка").created(LocalDateTime.now()).items(List.of()).build();

        List<ItemRequestDto> responseBody = List.of(r1, r2);

        Mockito.when(itemRequestClient.getUserRequests(1L)).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[0].description").value("Нужен велосипед")).andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /requests/all должен возвращать запросы других пользователей с пагинацией")
    void getAll_shouldReturnList() throws Exception {
        ItemRequestDto r1 = ItemRequestDto.builder().id(10L).description("Нужна дрель").created(LocalDateTime.now()).items(List.of()).build();

        List<ItemRequestDto> responseBody = List.of(r1);

        Mockito.when(itemRequestClient.getAllRequests(1L, 0, 10)).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L).param("from", "0").param("size", "10")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id").value(10L)).andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    @DisplayName("GET /requests/{id} должен возвращать запрос с вещами")
    void getById_shouldReturnRequestWithItems() throws Exception {
        ItemForRequestDto itemDto = ItemForRequestDto.builder().id(5L).name("Отвертка").ownerId(2L).requestId(3L).build();

        ItemRequestDto responseBody = ItemRequestDto.builder().id(3L).description("Нужна отвертка").created(LocalDateTime.now()).items(List.of(itemDto)).build();

        Mockito.when(itemRequestClient.getById(1L, 3L)).thenReturn(ResponseEntity.ok(responseBody));

        mockMvc.perform(get("/requests/{requestId}", 3L).header("X-Sharer-User-Id", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(3L)).andExpect(jsonPath("$.description").value("Нужна отвертка")).andExpect(jsonPath("$.items", hasSize(1))).andExpect(jsonPath("$.items[0].id").value(5L)).andExpect(jsonPath("$.items[0].name").value("Отвертка")).andExpect(jsonPath("$.items[0].ownerId").value(2L)).andExpect(jsonPath("$.items[0].requestId").value(3L));
    }
}
