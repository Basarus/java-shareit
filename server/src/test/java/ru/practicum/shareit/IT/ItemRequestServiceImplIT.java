package ru.practicum.shareit.IT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIT {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("create должен создавать запрос и возвращать DTO без items")
    void create_shouldCreateRequest() {
        User user = userRepository.save(User.builder()
                .name("user1")
                .email("user1@mail.com")
                .build());

        ItemRequestDto input = ItemRequestDto.builder()
                .description("Нужен дрель")
                .build();

        ItemRequestDto result = itemRequestService.create(user.getId(), input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужен дрель");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems()).isEmpty();

        assertThat(requestRepository.findById(result.getId())).isPresent();
    }

    @Test
    @DisplayName("getUserRequests должен возвращать запросы пользователя с вещами, отсортированные по created DESC")
    void getUserRequests_shouldReturnOwnRequestsWithItems() {
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        User requester = userRepository.save(User.builder()
                .name("requester")
                .email("requester@mail.com")
                .build());

        ItemRequest first = requestRepository.save(ItemRequest.builder()
                .description("Нужен ноутбук")
                .requestor(requester.getId())
                .created(LocalDateTime.now().minusDays(2))
                .build());

        ItemRequest second = requestRepository.save(ItemRequest.builder()
                .description("Нужен пылесос")
                .requestor(requester.getId())
                .created(LocalDateTime.now().minusDays(1))
                .build());

        itemRepository.save(Item.builder()
                .name("Ноут Asus")
                .description("Игровой")
                .available(true)
                .owner(owner)
                .request(first.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("Пылесос Samsung")
                .description("Мощный")
                .available(true)
                .owner(owner)
                .request(second.getId())
                .build());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requester.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужен пылесос");
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Пылесос Samsung");

        assertThat(result.get(1).getDescription()).isEqualTo("Нужен ноутбук");
        assertThat(result.get(1).getItems()).hasSize(1);
        assertThat(result.get(1).getItems().get(0).getName()).isEqualTo("Ноут Asus");
    }

    @Test
    @DisplayName("getAllRequests должен возвращать запросы других пользователей с пагинацией")
    void getAllRequests_shouldReturnRequestsOfOthers() {
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner2@mail.com")
                .build());

        User requester = userRepository.save(User.builder()
                .name("requester")
                .email("requester2@mail.com")
                .build());

        User other = userRepository.save(User.builder()
                .name("other")
                .email("other@mail.com")
                .build());

        ItemRequest r1 = requestRepository.save(ItemRequest.builder()
                .description("Нужна газонокосилка")
                .requestor(other.getId())
                .created(LocalDateTime.now().minusHours(3))
                .build());

        ItemRequest r2 = requestRepository.save(ItemRequest.builder()
                .description("Нужен перфоратор")
                .requestor(other.getId())
                .created(LocalDateTime.now().minusHours(1))
                .build());

        itemRepository.save(Item.builder()
                .name("Газонокосилка Bosch")
                .description("Электрическая")
                .available(true)
                .owner(owner)
                .request(r1.getId())
                .build());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requester.getId(), 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужен перфоратор");
        assertThat(result.get(1).getDescription()).isEqualTo("Нужна газонокосилка");
    }

    @Test
    @DisplayName("getById должен возвращать запрос с вещами")
    void getById_shouldReturnRequestWithItems() {
        User owner = userRepository.save(User.builder()
                .name("owner3")
                .email("owner3@mail.com")
                .build());

        User requester = userRepository.save(User.builder()
                .name("requester3")
                .email("requester3@mail.com")
                .build());

        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("Нужен велосипед")
                .requestor(requester.getId())
                .created(LocalDateTime.now())
                .build());

        itemRepository.save(Item.builder()
                .name("Горный велосипед")
                .description("27 скоростей")
                .available(true)
                .owner(owner)
                .request(request.getId())
                .build());

        ItemRequestDto result = itemRequestService.getById(requester.getId(), request.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo("Нужен велосипед");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Горный велосипед");
    }

    @Test
    @DisplayName("create должен бросать исключение, если пользователь не найден")
    void create_shouldThrowIfUserNotFound() {
        ItemRequestDto input = ItemRequestDto.builder()
                .description("Нужен телевизор")
                .build();

        assertThrows(RuntimeException.class,
                () -> itemRequestService.create(999L, input));
    }
}
