package xyz.fay.runner.utils;

import org.springframework.lang.NonNull;

import java.nio.file.Path;

public class PathUtils {
    private PathUtils() {}

    public static String getFileName(@NonNull Path path) {
        return path.getFileName().toString();
    }

    public static Boolean filterHiddenPath(@NonNull Path path) {
        return !path.toFile().isHidden();
    }
}
