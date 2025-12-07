package ru.practicum.shareit.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @DisplayName("ItemDto корректно сериализуется и десериализуется")
    void itemDtoJsonTest() throws Exception {
        ItemDto dto = ItemDto.builder().id(1L).name("Дрель").description("Аккумуляторная").available(true).requestId(5L).build();

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Аккумуляторная");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);

        ItemDto parsed = json.parseObject(content.getJson());
        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getName()).isEqualTo("Дрель");
        assertThat(parsed.getDescription()).isEqualTo("Аккумуляторная");
        assertThat(parsed.getAvailable()).isTrue();
        assertThat(parsed.getRequestId()).isEqualTo(5L);
    }
}
