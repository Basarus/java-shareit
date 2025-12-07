package ru.practicum.shareit.booking;

import ru.practicum.shareit.common.BadRequestException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + value);
        }
    }
}
