package ru.practicum.shareit.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @DisplayName("BookingDto корректно сериализуется и десериализуется")
    void bookingDtoJsonTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 13, 0);

        BookingDto dto = BookingDto.builder().id(1L).itemId(2L).bookerId(3L).start(start).end(end).status(BookingStatus.WAITING).build();

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);
        assertThat(content).extractingJsonPathStringValue("$.start").startsWith("2025-01-01T12:00");
        assertThat(content).extractingJsonPathStringValue("$.end").startsWith("2025-01-01T13:00");
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        BookingDto parsed = json.parseObject(content.getJson());
        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}