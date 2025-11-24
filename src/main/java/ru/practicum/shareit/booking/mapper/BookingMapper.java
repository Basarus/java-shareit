package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toEntity(BookingDto dto, Item item, User booker) {
        if (dto == null) return null;

        return Booking.builder().id(dto.getId()).start(dto.getStart()).end(dto.getEnd()).item(item).booker(booker).status(dto.getStatus()).build();
    }

    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        BookingDto dto = BookingDto.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd()).status(booking.getStatus()).itemId(booking.getItem() != null ? booking.getItem().getId() : null).bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null).build();

        if (booking.getItem() != null) {
            ItemDto itemDto = ItemMapper.toDto(booking.getItem());
            dto.setItem(itemDto);
        }

        if (booking.getBooker() != null) {
            UserDto userDto = UserMapper.toDto(booking.getBooker());
            dto.setBooker(userDto);
        }

        return dto;
    }
}
