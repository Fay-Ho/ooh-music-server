package xyz.fay.runner.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.fay.runner.model.MusicQueryResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MusicService {
    private final Executor asyncTaskExecutor;

    public MusicService(Executor asyncTaskExecutor) {
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    public CompletableFuture<List<MusicQueryResponse>> getList(String type) {
        return CompletableFuture.supplyAsync(() -> {
            Path dirPath = getDirPath(type);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) return null;

            File[] files = dirPath.toFile().listFiles();
            if (files == null || files.length == 0) return null;

            return Arrays.stream(files)
                .filter(f -> !f.isHidden())
                .map(this::setResponseBody)
                .sorted(Comparator.comparing(MusicQueryResponse::getDisplay, Collator.getInstance(Locale.CHINESE)))
                .collect(Collectors.toList());

        }, asyncTaskExecutor);
    }

    public CompletableFuture<Resource> getMusic(@Nullable String type, @NonNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (type == null) return null;

            Path dirPath = getDirPath(type);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) return null;

            try (Stream<Path> pathStream = Files.list(dirPath)) {
                Optional<Path> path = pathStream
                    .filter(p -> p.getFileName().toString().contains(name))
                    .findFirst();
                return path
                    .map(FileSystemResource::new)
                    .orElse(null);
            } catch (IOException e) {
                return null;
            }

        }, asyncTaskExecutor);
    }

    @NonNull
    private Path getDirPath(@Nullable String subPath) {
        String userHome = System.getProperty("user.home");
        Path musicRoot = Paths.get(userHome).resolve("Music").toAbsolutePath().normalize();
        if (subPath == null || subPath.isEmpty()) return musicRoot;
        Path musicPath = musicRoot.resolve(subPath).toAbsolutePath().normalize();
        if (!musicPath.startsWith(musicRoot)) return musicRoot;
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
