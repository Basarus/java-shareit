package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toEntity(Long requestorId, ItemRequestDto dto) {
        if (dto == null) return null;

        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(requestorId)
                .created(dto.getCreated() != null ? dto.getCreated() : LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest entity, List<Item> items) {
        if (entity == null) return null;

        List<ItemForRequestDto> itemDtos = items == null ? Collections.emptyList() :
                items.stream()
                        .map(ItemRequestMapper::toItemForRequestDto)
                        .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .items(itemDtos)
                .build();
    }

    private static ItemForRequestDto toItemForRequestDto(Item item) {
        if (item == null) return null;

        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest())
                .build();
    }
}
