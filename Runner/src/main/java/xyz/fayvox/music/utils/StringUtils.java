package xyz.fayvox.music.utils;

public final class StringUtils {
    private StringUtils() {}

    public static String substringBefore(final String string, final String separator) {
        if (string == null || string.isEmpty() || separator == null) {
            return string;
        }

        if (separator.isEmpty()) {
            return "";
        }

        final int pos = string.indexOf(separator);
        if (pos == -1) {
            return string;
        }

        return string.substring(0, pos);
    }
}
