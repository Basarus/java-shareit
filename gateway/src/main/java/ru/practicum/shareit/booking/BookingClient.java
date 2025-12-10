package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory("http://shareit-server:9090" + API_PREFIX)).build());
    }

    public ResponseEntity<Object> create(Long userId, BookingDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, ownerId, null);
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByBooker(Long userId, String state) {
        String path = "?state=" + state;
        return get(path, userId);
    }

    public ResponseEntity<Object> getByOwner(Long userId, String state) {
        String path = "/owner?state=" + state;
        return get(path, userId);
    }
}
