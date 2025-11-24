package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toEntity(BookingDto dto, Item item, User booker) {
        if (dto == null) return null;
        return Booking.builder().id(dto.getId()).start(dto.getStart()).end(dto.getEnd()).item(item).booker(booker).status(dto.getStatus()).build();
    }

    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd()).itemId(booking.getItem().getId()).bookerId(booking.getBooker().getId()).status(booking.getStatus()).build();
    }

    public static BookingShortDto toShortDto(Booking booking) {
        if (booking == null) return null;
        return BookingShortDto.builder().id(booking.getId()).bookerId(booking.getBooker().getId()).start(booking.getStart()).end(booking.getEnd()).build();
    }
}
