package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()
                        )
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long ownerId, ItemDto dto) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> update(Long ownerId, Long itemId, ItemDto dto) {
        return patch("/" + itemId, ownerId, dto);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(Long userId, String text) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
