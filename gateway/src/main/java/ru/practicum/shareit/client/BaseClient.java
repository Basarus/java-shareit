package ru.practicum.shareit.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class BaseClient {

    private final RestTemplate restTemplate;


    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method,
            String path,
            Long userId,
            T body
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }
}
