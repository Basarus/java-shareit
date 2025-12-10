package ru.practicum.shareit.IT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceImplIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("create + getAll должны сохранять и возвращать пользователей")
    void createAndGetAll() {
        UserDto dto = UserDto.builder()
                .name("user")
                .email("user@mail.com")
                .build();

        UserDto created = userService.create(dto);

        assertThat(created.getId()).isNotNull();

        List<UserDto> all = userService.getAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getEmail()).isEqualTo("user@mail.com");
    }

    @Test
    @DisplayName("update должен изменять данные пользователя")
    void update_shouldChangeUser() {
        User saved = userRepository.save(User.builder()
                .name("old")
                .email("old@mail.com")
                .build());

        UserDto patch = UserDto.builder()
                .name("new")
                .build();

        UserDto updated = userService.update(saved.getId(), patch);

        assertThat(updated.getName()).isEqualTo("new");
        assertThat(updated.getEmail()).isEqualTo("old@mail.com");
    }

    @Test
    @DisplayName("delete должен удалять пользователя, а get бросать NotFoundException")
    void delete_shouldRemoveUser() {
        User saved = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        userService.delete(saved.getId());

        assertThrows(NotFoundException.class,
                () -> userService.get(saved.getId()));
    }
}