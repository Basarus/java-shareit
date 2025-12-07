package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid BookingDto dto) {
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getByOwner(userId, state);
    }
}
