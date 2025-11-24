package xyz.fay.runner.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.fay.runner.ext.AudioType;
import xyz.fay.runner.model.MusicQuery;
import xyz.fay.runner.model.MusicQueryResponse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MusicController {
    @RequestMapping("/music")
    public ResponseEntity<?> request(@NonNull MusicQuery query) {
        if (query.getName() == null) return getList(query.getType());
        return getMusic(query.getType(), query.getName());
    }

    private ResponseEntity<Resource> getMusic(@Nullable String type, @NonNull String name) {
        if (type == null) return ResponseEntity.notFound().build();

        Path dirPath = getDirPath(type);
        if (!Files.exists(dirPath)) return ResponseEntity.notFound().build();

        File dir = dirPath.toFile();
        if (!dir.isDirectory()) return ResponseEntity.notFound().build();

        File[] files = dir.listFiles((d, n) -> n.contains(name));
        if (files == null) return ResponseEntity.notFound().build();

        String fileName = files[0].getName();
        Path filePath = getDirPath(type).resolve(fileName);
        if (!Files.exists(filePath)) return ResponseEntity.notFound().build();

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity
            .ok()
            .contentType(AudioType.FLAC)
            .body(resource);
    }

    private ResponseEntity<Map<String, List<MusicQueryResponse>>> getList(@Nullable String type) {
        Path dirPath = getDirPath(type);
        if (!Files.exists(dirPath)) return ResponseEntity.notFound().build();

        File dir = dirPath.toFile();
        if (!dir.isDirectory()) return ResponseEntity.notFound().build();

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Collections.singletonMap("data",
                Arrays.stream(files)
                    .filter(f -> !f.isHidden())
                    .map(this::setResponseBody)
                    .sorted(Comparator.comparing(MusicQueryResponse::getDisplay))
                    .collect(Collectors.toList())
                )
            );
    }

    @NonNull
    private Path getDirPath(@Nullable String subPath) {
        String userHome = System.getProperty("user.home");
        Path musicPath = Paths.get(userHome).resolve("Music");
        if (subPath != null) return musicPath.resolve(subPath);
        return musicPath;
    }

    @NonNull
    private MusicQueryResponse setResponseBody(@NonNull File file) {
        String fileName = file.getName();
        return new MusicQueryResponse(
            FilenameUtils.getBaseName(fileName),
            FilenameUtils.getExtension(fileName),
            fileName,
            file.getParentFile().getName()
        );
    }
}
