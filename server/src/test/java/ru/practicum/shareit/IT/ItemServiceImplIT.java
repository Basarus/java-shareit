package ru.practicum.shareit.IT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceImplIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("create + get должны сохранять и возвращать вещь с владельцем")
    void createAndGet() {
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        ItemDto dto = ItemDto.builder()
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .build();

        ItemDto created = itemService.create(owner.getId(), dto);

        assertThat(created.getId()).isNotNull();

        ItemDto found = itemService.get(owner.getId(), created.getId());

        assertThat(found.getName()).isEqualTo("Дрель");
        assertThat(found.getDescription()).isEqualTo("Аккумуляторная");

        Item entity = itemRepository.findById(created.getId()).orElseThrow();
        assertThat(entity.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    @DisplayName("getByOwner должен возвращать все вещи владельца")
    void getByOwner_shouldReturnItems() {
        User owner = userRepository.save(User.builder()
                .name("owner2")
                .email("owner2@mail.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Дрель")
                .description("1")
                .available(true)
                .owner(owner)
                .build());

        itemRepository.save(Item.builder()
                .name("Отвертка")
                .description("2")
                .available(true)
                .owner(owner)
                .build());

        List<ItemDto> result = itemService.getByOwner(owner.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("search должен находить вещи по тексту")
    void search_shouldFindItems() {
        User owner = userRepository.save(User.builder()
                .name("owner3")
                .email("owner3@mail.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .owner(owner)
                .build());

        List<ItemDto> result = itemService.search(owner.getId(), "дрель");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }
}