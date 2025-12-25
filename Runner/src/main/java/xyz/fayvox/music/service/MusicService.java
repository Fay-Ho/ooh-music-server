package xyz.fayvox.music.service;

import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.fayvox.music.common.Optional;
import xyz.fayvox.music.common.constant.MusicCategory;
import xyz.fayvox.music.model.MusicQuery;
import xyz.fayvox.music.model.MusicResponse;
import xyz.fayvox.music.utils.PathUtils;
import xyz.fayvox.music.utils.StringUtils;

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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public final class MusicService {
    private static final String NAME_SEPARATOR = " - ";
    private static final String MUSIC_DIR = "Music";

    private final Executor asyncTaskExecutor;

    public CompletableFuture<List<MusicResponse>> getList(@NonNull MusicQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            Path dirPath = getDirPath(query.getType());
            if (!Files.isDirectory(dirPath)) return null;

            try (Stream<Path> pathStream = Files.list(dirPath)) {
                return pathStream
                    .filter(getPathPredicate(query))
                    .sorted(Comparator.comparing(PathUtils::getFileName, Collator.getInstance(Locale.CHINESE)))
                    .map(p -> setResponseBody(query, p))
                    .distinct()
                    .collect(Collectors.toList());
            } catch (IOException e) {
                return null;
            }
        }, asyncTaskExecutor);
    }

    public CompletableFuture<Resource> getMusic(@NonNull MusicQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            if (query.getType() == null) return null;

            Path dirPath = getDirPath(query.getType());
            if (!Files.isDirectory(dirPath)) return null;

            try (Stream<Path> pathStream = Files.list(dirPath)) {
                return pathStream
                    .filter(getPathPredicate(query))
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
        return musicPath.startsWith(musicRoot) ? musicPath : musicRoot;
    }

    @NonNull
    private Predicate<Path> getPathPredicate(@NonNull MusicQuery query) {
        Function<Function<Path, String>, Function<String, Predicate<Path>>> fn1 =
            f -> s -> ((Predicate<Path>) PathUtils::nonHiddenFile).and(p -> f.apply(p).contains(s));

        Function<Path, String> fn2 =
            p -> StringUtils.substringBefore(PathUtils.getFileName(p), NAME_SEPARATOR);

        return Optional
            .ofNullable(query.getArtist())
            .map(fn1.apply(fn2))
            .orElseNullable(query.getName())
            .orElse(query.getSearch())
            .map(fn1.apply(PathUtils::getBaseName))
            .orDefault(PathUtils::nonHiddenFile);
    }

    @NonNull
    private MusicResponse setResponseBody(@NonNull MusicQuery query, @NonNull Path path) {
        String fileName = PathUtils.getFileName(path);
        String artist = StringUtils.substringBefore(fileName, NAME_SEPARATOR);

        if (query.getType() == null) return MusicResponse.builder().type(FilenameUtils.getBaseName(fileName)).build();

        if (MusicCategory.ARTISTS.equals(query.getCategory())) return MusicResponse.builder().artist(artist).build();

        return MusicResponse
            .builder()
            .artist(artist)
            .ext(FilenameUtils.getExtension(fileName))
            .fullName(fileName)
            .name(FilenameUtils.getBaseName(fileName))
            .type(PathUtils.getFileName(path.getParent()))
            .build();
    }
}
