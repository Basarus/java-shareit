package ru.practicum.shareit.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @DisplayName("UserDto корректно сериализуется и десериализуется")
    void userDtoJsonTest() throws Exception {
        UserDto dto = UserDto.builder().id(1L).name("user").email("user@mail.com").build();

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.com");

        UserDto parsed = json.parseObject(content.getJson());
        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getName()).isEqualTo("user");
        assertThat(parsed.getEmail()).isEqualTo("user@mail.com");
    }
}
