package xyz.fay.runner.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.fay.runner.enums.Category;
import xyz.fay.runner.model.MusicQuery;
import xyz.fay.runner.model.MusicQueryResponse;
import xyz.fay.runner.utils.PathUtils;
import xyz.fay.runner.utils.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MusicService {
    private static final String NAME_SEPARATOR = " - ";
    private static final String MUSIC_DIR = "Music";

    private final Executor asyncTaskExecutor;

    public MusicService(Executor asyncTaskExecutor) {
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    public CompletableFuture<List<MusicQueryResponse>> getList(@NonNull MusicQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            Path dirPath = getDirPath(query.getType());
            if (!Files.isDirectory(dirPath)) return null;

            try (Stream<Path> pathStream = Files.list(dirPath)) {
                Predicate<Path> filter = getPathPredicate(query);

                final Comparator<MusicQueryResponse> comparator = Comparator.comparing(
                    MusicQueryResponse::getName,
                    Collator.getInstance(Locale.CHINESE)
                );

                if (query.getCategory() != null && query.getCategory().contentEquals(Category.ARTISTS)) {
                    return pathStream
                        .filter(filter)
                        .map(p -> StringUtils.substringBefore(PathUtils.getFileName(p), NAME_SEPARATOR))
                        .distinct()
                        .map(this::setArtistsResponseBody)
                        .sorted(comparator)
                        .collect(Collectors.toList());
                }

                return pathStream
                    .filter(filter)
                    .map(this::setResponseBody)
                    .sorted(comparator)
                    .collect(Collectors.toList());
            } catch (IOException e) {
                return null;
            }
        }, asyncTaskExecutor);
    }

    public CompletableFuture<Resource> getMusic(@Nullable String type, @NonNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (type == null) return null;

            Path dirPath = getDirPath(type);
            if (!Files.isDirectory(dirPath)) return null;

            try (Stream<Path> pathStream = Files.list(dirPath)) {
                return pathStream
                    .filter(PathUtils::filterHiddenPath)
                    .filter(p -> PathUtils.getFileName(p).contains(name))
                    .findFirst()
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
        Path musicRoot = Paths.get(userHome).resolve(MUSIC_DIR).toAbsolutePath().normalize();
        if (subPath == null || subPath.isEmpty()) return musicRoot;
        Path musicPath = musicRoot.resolve(subPath).toAbsolutePath().normalize();
        if (!musicPath.startsWith(musicRoot)) return musicRoot;
        return musicPath;
    }

    @NonNull
    private static Predicate<Path> getPathPredicate(@NonNull MusicQuery query) {
        Predicate<Path> filter = PathUtils::filterHiddenPath;
        if (query.getArtist() != null) {
            filter = filter.and(p -> StringUtils.substringBefore(PathUtils.getFileName(p), NAME_SEPARATOR).contains(query.getArtist()));
        } else if (query.getSearch() != null) {
            filter = filter.and(p -> PathUtils.getFileName(p).contains(query.getSearch()));
        }
        return filter;
    }

    private MusicQueryResponse setArtistsResponseBody(@NonNull String string) {
        return new MusicQueryResponse(
            null,
            null,
            null,
            string,
            null
        );
    }

    @NonNull
    private MusicQueryResponse setResponseBody(@NonNull Path path) {
        String fileName = PathUtils.getFileName(path);
        return new MusicQueryResponse(
            StringUtils.substringBefore(fileName, NAME_SEPARATOR),
            FilenameUtils.getExtension(fileName),
            fileName,
            FilenameUtils.getBaseName(fileName),
            PathUtils.getFileName(path.getParent())
        );
    }
}
