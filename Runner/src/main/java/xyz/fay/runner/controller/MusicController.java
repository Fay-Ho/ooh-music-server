package xyz.fay.runner.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.fay.runner.ext.AudioType;
import xyz.fay.runner.model.MusicQuery;
import xyz.fay.runner.model.MusicQueryResponse;
import xyz.fay.runner.service.MusicService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class MusicController {
    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/api/music")
    public CompletableFuture<ResponseEntity<?>> request(@NonNull MusicQuery query) {
        if (query.getName() == null) return getList(query.getType()).thenApply(r -> r);
        return getMusic(query.getType(), query.getName()).thenApply(r -> r);
    }

    private CompletableFuture<ResponseEntity<Resource>> getMusic(@Nullable String type, @NonNull String name) {
        return musicService.getMusic(type, name)
            .thenApply(resource -> {
                if (resource == null) return ResponseEntity.notFound().build();
                AudioType audioType = AudioType.getFromFileExtension(resource.getFilename());
                if (audioType == null) return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
                return ResponseEntity.ok().contentType(audioType).body(resource);
            });
    }

    private CompletableFuture<ResponseEntity<Map<String, List<MusicQueryResponse>>>> getList(@Nullable String type) {
        return musicService.getList(type)
            .thenApply(list -> {
                if (list == null || list.isEmpty()) return ResponseEntity.notFound().build();
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Collections.singletonMap("data", list));
            });
    }
}
