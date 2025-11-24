package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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

    public static Item fromDto(ItemDto d, Long ownerId) {
        if (d == null) return null;
        return Item.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .available(d.getAvailable())
                .owner(ownerId)
                .request(d.getRequestId())
                .build();
    }
}