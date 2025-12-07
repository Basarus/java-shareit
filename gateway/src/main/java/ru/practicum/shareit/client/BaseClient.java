package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class BaseClient {

    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }


    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        return rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body);
    }


    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        return rest.exchange(path, HttpMethod.PATCH, requestEntity, Object.class);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, body);
    }


    protected ResponseEntity<Object> get(String path, Long userId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders(userId));
        return rest.exchange(path, HttpMethod.GET, requestEntity, Object.class);
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, (Long) null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> params) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders(userId));
        return rest.exchange(path, HttpMethod.GET, requestEntity, Object.class, params);
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> params) {
        return get(path, null, params);
    }


    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders(userId));
        return rest.exchange(path, HttpMethod.DELETE, requestEntity, Object.class);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null);
    }


    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
