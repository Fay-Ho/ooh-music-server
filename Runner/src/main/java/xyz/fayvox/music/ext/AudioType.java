package xyz.fayvox.music.ext;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.util.Map;

public final class AudioType extends MediaType {
    public static final MediaType AUDIO_AAC;
    private static final String AUDIO_AAC_VALUE = "audio/aac";
    public static final MediaType AUDIO_FLAC;
    private static final String AUDIO_FLAC_VALUE = "audio/flac";
    public static final MediaType AUDIO_MPEG;
    private static final String AUDIO_MPEG_VALUE = "audio/mpeg";
    public static final MediaType AUDIO_WAV;
    private static final String AUDIO_WAV_VALUE = "audio/wav";

    static {
        AUDIO_AAC = valueOf(AUDIO_AAC_VALUE);
        AUDIO_FLAC = valueOf(AUDIO_FLAC_VALUE);
        AUDIO_MPEG = valueOf(AUDIO_MPEG_VALUE);
        AUDIO_WAV = valueOf(AUDIO_WAV_VALUE);
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
}
