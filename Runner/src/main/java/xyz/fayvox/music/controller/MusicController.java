package xyz.fayvox.music.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.fayvox.music.common.Optional;
import xyz.fayvox.music.common.response.ApiResponse;
import xyz.fayvox.music.exception.NotFoundException;
import xyz.fayvox.music.exception.UnsupportedMediaTypeException;
import xyz.fayvox.music.model.MusicBody;
import xyz.fayvox.music.model.MusicQuery;
import xyz.fayvox.music.model.MusicResponse;
import xyz.fayvox.music.service.MusicService;
import xyz.fayvox.music.utils.AudioTypeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@AllArgsConstructor
@RestController
@RequestMapping("/api/music")
public final class MusicController {
    private final MusicService musicService;

    @GetMapping
    public CompletableFuture<? extends ResponseEntity<?>> request(@NonNull MusicQuery query) {
        if (query.getName() == null) return getList(query);
        return getMusic(query);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Map<String, List<MusicResponse>>>> request(@NonNull MusicBody body) {
        return null;
    }

    private CompletableFuture<ResponseEntity<Map<String, List<MusicResponse>>>> getList(@NonNull MusicQuery query) {
        return musicService
            .getList(query)
            .thenApply(l -> Optional
                .ofNullable(l)
                .filter(li -> !li.isEmpty())
                .map(ApiResponse::ok)
                .orElseThrow(NotFoundException::new));
    }

    private CompletableFuture<ResponseEntity<Resource>> getMusic(@NonNull MusicQuery query) {
        Function<Resource, MediaType> fn = r -> Optional
            .ofNullable(AudioTypeUtils.getMediaTypeByFileName(r.getFilename()))
            .orElseThrow(UnsupportedMediaTypeException::new);

        return musicService
            .getMusic(query)
            .thenApply(r -> Optional
                .ofNullable(r)
                .map(m -> ApiResponse.ok(m, fn.apply(r)))
                .orElseThrow(NotFoundException::new));
    }
}
