package ru.practicum.shareit.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResult {
    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public static ErrorResult of(HttpStatus status, String message, String path) {
        return ErrorResult.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
