package ru.practicum.shareit.IT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookingServiceImplIT {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("create + get должны создавать и возвращать бронирование со статусом WAITING")
    void createAndGet() {
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("booker")
                .email("booker@mail.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .owner(owner)
                .build());

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .build();

        BookingDto created = bookingService.create(booker.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        BookingDto found = bookingService.get(booker.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getItemId()).isEqualTo(item.getId());
        assertThat(found.getBookerId()).isEqualTo(booker.getId());
    }
}