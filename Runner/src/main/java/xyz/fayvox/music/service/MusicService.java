package xyz.fayvox.music.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.fayvox.music.common.Optional;
import xyz.fayvox.music.enums.Category;
import xyz.fayvox.music.model.MusicQuery;
import xyz.fayvox.music.model.MusicQueryResponse;
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

@Service
public final class MusicService {
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
        Function<Function<Path, String>, Function<String, Predicate<Path>>> function =
            f -> s -> ((Predicate<Path>) PathUtils::nonHiddenFile).and(p -> f.apply(p).contains(s));

        Function<Path, String> getArtistName =
            p -> StringUtils.substringBefore(PathUtils.getFileName(p), NAME_SEPARATOR);

        return Optional
            .ofNullable(query.getArtist())
            .map(function.apply(getArtistName))
            .orDefaultGet(() -> Optional
                .ofNullable(query.getName())
                .orElse(query.getSearch())
                .map(function.apply(PathUtils::getBaseName))
                .orDefault(PathUtils::nonHiddenFile)
            );
    }

    @NonNull
    private MusicQueryResponse setResponseBody(@NonNull MusicQuery query, @NonNull Path path) {
        String fileName = PathUtils.getFileName(path);
        String artist = StringUtils.substringBefore(fileName, NAME_SEPARATOR);

        if (query.getType() == null) return MusicQueryResponse.withType(FilenameUtils.getBaseName(fileName));

        if (Category.ARTISTS.equals(query.getCategory())) return MusicQueryResponse.withArtist(artist);

        return new MusicQueryResponse(
            artist,
            FilenameUtils.getExtension(fileName),
            fileName,
            FilenameUtils.getBaseName(fileName),
            PathUtils.getFileName(path.getParent())
        );
    }
}
