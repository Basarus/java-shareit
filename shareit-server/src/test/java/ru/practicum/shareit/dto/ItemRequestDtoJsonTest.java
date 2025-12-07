package ru.practicum.shareit.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @DisplayName("ItemRequestDto должен корректно сериализоваться и десериализоваться в JSON")
    void itemRequestDtoJsonTest() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 12, 30, 0);

        ItemForRequestDto itemDto = ItemForRequestDto.builder().id(5L).name("Отвертка").ownerId(2L).requestId(3L).build();

        ItemRequestDto dto = ItemRequestDto.builder().id(3L).description("Нужна отвертка").created(created).items(List.of(itemDto)).build();

        var jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Нужна отвертка");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").startsWith("2025-01-01T12:30:00");
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Отвертка");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(3);

        String content = jsonContent.getJson();
        ItemRequestDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(3L);
        assertThat(parsed.getDescription()).isEqualTo("Нужна отвертка");
        assertThat(parsed.getCreated()).isEqualTo(created);
        assertThat(parsed.getItems()).hasSize(1);
        assertThat(parsed.getItems().get(0).getId()).isEqualTo(5L);
    }
}
