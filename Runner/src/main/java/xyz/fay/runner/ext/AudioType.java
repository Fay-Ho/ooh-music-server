package xyz.fay.runner.ext;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class AudioType extends MediaType {
    private static final String AUDIO_STRING = "audio";
    private static final String AAC_STRING = "aac";
    private static final String FLAC_STRING = "flac";
    private static final String MP3_STRING = "mp3";
    private static final String WAV_STRING = "wav";
    private static final Map<String, AudioType> EXTENSION_MAP = new HashMap<>();

    public static final AudioType AAC;
    public static final AudioType FLAC;
    public static final AudioType MPEG;
    public static final AudioType WAV;

    static {
        AAC = new AudioType(AUDIO_STRING, "aac");
        FLAC = new AudioType(AUDIO_STRING, "flac");
        MPEG = new AudioType(AUDIO_STRING, "mpeg");
        WAV = new AudioType(AUDIO_STRING, "wav");

        EXTENSION_MAP.put(AAC_STRING, AAC);
        EXTENSION_MAP.put(FLAC_STRING, FLAC);
        EXTENSION_MAP.put(MP3_STRING, MPEG);
        EXTENSION_MAP.put(WAV_STRING, WAV);
    }

    public AudioType(String type) {
        super(type);
    }

    public AudioType(String type, String subtype) {
        super(type, subtype);
    }

    public AudioType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public AudioType(String type, String subtype, double qualityValue) {
        super(type, subtype, qualityValue);
    }

    public AudioType(MediaType other, Charset charset) {
        super(other, charset);
    }

    public AudioType(MediaType other, Map<String, String> parameters) {
        super(other, parameters);
    }

    public AudioType(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public AudioType(MimeType mimeType) {
        super(mimeType);
    }

    @Nullable
    public static AudioType getFromFileExtension(String filePath) {
        String extension = FilenameUtils.getExtension(filePath).toLowerCase();
        if (extension.isEmpty()) return null;
        return EXTENSION_MAP.get(extension);
    }
}
