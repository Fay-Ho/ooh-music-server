package xyz.fayvox.music.common.response;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

public final class ApiResponse {
    public static <T> ResponseEntity<Map<String, T>> ok(T data) {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Collections.singletonMap("data", data));
    }

    public static <T extends Resource> ResponseEntity<T> ok(T data, MediaType mediaType) {
        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .body(data);
    }
}
