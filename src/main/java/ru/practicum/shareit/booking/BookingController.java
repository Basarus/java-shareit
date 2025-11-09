package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @PostMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingDto dto) {
        return dto;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return new BookingDto();
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return new BookingDto();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public List<BookingDto> list(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return List.of();
    }
}