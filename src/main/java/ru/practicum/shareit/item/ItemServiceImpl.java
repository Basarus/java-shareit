package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private final UserService users;

    public ItemServiceImpl(UserService users) {
        this.users = users;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        requireUserHeader(ownerId);
        if (!users.exists(ownerId)) throw new NotFoundException("Owner not found:" + ownerId);
        if (dto == null) throw new BadRequestException("body must not be null ");
        if (dto.getAvailable() == null) throw new BadRequestException("available must not be null");
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("name must not be blank");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new BadRequestException("description must not be blank");
        Long id = seq.incrementAndGet();
        Item i = ItemMapper.fromDto(dto, ownerId);
        i.setId(id);
        storage.put(id, i);
        return ItemMapper.toDto(i);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        requireUserHeader(ownerId);
        Item i = storage.get(itemId);
        if (i == null) throw new NotFoundException("Item not found: " + itemId);
        if (!Objects.equals(i.getOwner(), ownerId))
            throw new NotFoundException("Item not found for this owner: " + itemId);
        if (patch.getName() != null) {
            if (patch.getName().isBlank()) throw new BadRequestException("name must not be blank");
            i.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            if (patch.getDescription().isBlank()) throw new BadRequestException("description must not be blank");
            i.setDescription(patch.getDescription());
        }
        ;
        if (patch.getAvailable() != null) {
            i.setAvailable(patch.getAvailable());
        }
        return ItemMapper.toDto(i);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        Item i = storage.get(itemId);
        if (i == null) throw new NotFoundException("Item not found: " + itemId);
        return ItemMapper.toDto(i);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        requireUserHeader(ownerId);
        return storage.values().stream().filter(i -> Objects.equals(i.getOwner(), ownerId)).sorted(Comparator.comparing(Item::getId)).map(ItemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> search(Long requesterId, String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream().filter(Item::getAvailable).filter(i -> i.getName().toLowerCase().contains(q) || i.getDescription().toLowerCase().contains(q)).sorted(Comparator.comparing(Item::getId)).map(ItemMapper::toDto).toList();
    }

    private void requireUserHeader(Long userId) {
        if (userId == null) throw new BadRequestException("X-Sharer-User-Id header is required");
    }
}
