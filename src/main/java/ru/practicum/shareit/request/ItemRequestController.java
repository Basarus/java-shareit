package ru.practicum.shareit.request;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    @PostMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto dto) {
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public List<ItemRequestDto> my(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return List.of();
    }


    @GetMapping("/all")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public List<ItemRequestDto> all(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return List.of();
    }


    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ItemRequestDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return new ItemRequestDto();
    }
}