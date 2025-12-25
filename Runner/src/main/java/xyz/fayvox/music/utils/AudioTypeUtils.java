package xyz.fayvox.music.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import xyz.fayvox.music.ext.AudioType;

public final class AudioTypeUtils {
    private static final String AAC_VALUE = "aac";
    private static final String FLAC_VALUE = "flac";
    private static final String MP3_VALUE = "mp3";
    private static final String WAV_VALUE = "wav";

    @Nullable
    public static MediaType getMediaTypeByFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (extension.isEmpty()) return null;

        switch (extension) {
            case AAC_VALUE:
                return AudioType.AUDIO_AAC;
            case FLAC_VALUE:
                return AudioType.AUDIO_FLAC;
            case MP3_VALUE:
                return AudioType.AUDIO_MPEG;
            case WAV_VALUE:
                return AudioType.AUDIO_WAV;
            default:
                return null;
        }
    }
}
