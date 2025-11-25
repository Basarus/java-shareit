package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toDto(Item i) {
        if (i == null) return null;
        return ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .requestId(i.getRequest())
                .build();
    }

    public static ItemDto toDto(Item i, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        if (i == null) return null;
        return ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .requestId(i.getRequest())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item fromDto(ItemDto d, User owner) {
        if (d == null) return null;
        return Item.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .available(d.getAvailable())
                .owner(owner)
                .request(d.getRequestId())
                .build();
    }
}