package xyz.fayvox.music.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.lang.NonNull;

import java.nio.file.Path;

public final class PathUtils {
    private PathUtils() {}

    public static String getFileName(@NonNull Path path) {
        return path.getFileName().toString();
    }

    public static String getBaseName(@NonNull Path path) {
        return FilenameUtils.getBaseName(path.getFileName().toString());
    }

    public static Boolean nonHiddenFile(@NonNull Path path) {
        return !path.toFile().isHidden();
    }
}
