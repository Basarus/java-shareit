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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @DisplayName("POST /bookings должен создавать бронирование")
    void create_shouldReturnBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingDto input = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        BookingDto response = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .bookerId(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        Mockito.when(bookingService.create(eq(1L), any(BookingDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("PATCH /bookings/{id}?approved=true должен подтверждать бронирование")
    void approve_shouldReturnUpdatedBooking() throws Exception {
        BookingDto response = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingService.approve(1L, 1L, true))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /bookings/{id} должен возвращать бронирование")
    void get_shouldReturnBooking() throws Exception {
        BookingDto response = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        Mockito.when(bookingService.get(1L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /bookings должен возвращать список по booker’у")
    void listByBooker_shouldReturnList() throws Exception {
        BookingDto b = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        Mockito.when(bookingService.getByBooker(1L, "ALL"))
                .thenReturn(List.of(b));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /bookings/owner должен возвращать список по владельцу")
    void listByOwner_shouldReturnList() throws Exception {
        BookingDto b = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        Mockito.when(bookingService.getByOwner(1L, "ALL"))
                .thenReturn(List.of(b));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
