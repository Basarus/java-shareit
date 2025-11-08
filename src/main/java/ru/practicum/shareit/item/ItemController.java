package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    public ItemController(ItemService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @Valid @RequestBody ItemDto dto
    ) {
        ItemDto created = service.create(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto patch
    ) {
        return service.update(userId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long itemId
    ) {
        return service.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> ownerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam String text
    ) {
        return service.search(userId, text);
    }
}
