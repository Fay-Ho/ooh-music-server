package xyz.fayvox.music.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.fayvox.music.ext.AudioType;
import xyz.fayvox.music.model.MusicQuery;
import xyz.fayvox.music.model.MusicQueryResponse;
import xyz.fayvox.music.service.MusicService;
import xyz.fayvox.music.utils.AudioTypeUtils;

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
        if (query.getName() == null) return getList(query).thenApply(r -> r);
        return getMusic(query).thenApply(r -> r);
    }

    private CompletableFuture<ResponseEntity<Map<String, List<MusicQueryResponse>>>> getList(@NonNull MusicQuery query) {
        return musicService.getList(query)
            .thenApply(list -> {
                if (list == null || list.isEmpty()) return ResponseEntity.notFound().build();
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Collections.singletonMap("data", list));
            });
    }

    private CompletableFuture<ResponseEntity<Resource>> getMusic(@NonNull MusicQuery query) {
        return musicService.getMusic(query)
            .thenApply(resource -> {
                if (resource == null) return ResponseEntity.notFound().build();
                MediaType mediaType = AudioTypeUtils.getMediaTypeByFileName(resource.getFilename());
                if (mediaType == null) return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
                return ResponseEntity.ok().contentType(mediaType).body(resource);
            });
    }
}
