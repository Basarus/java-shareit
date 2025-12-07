package ru.practicum.shareit.user;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory("http://shareit-server:9090" + API_PREFIX)).build());
    }

    public ResponseEntity<Object> create(UserDto dto) {
        return post("", dto);
    }

    public ResponseEntity<Object> update(Long userId, UserDto dto) {
        return patch("/" + userId, dto);
    }

    public ResponseEntity<Object> get(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }
}
