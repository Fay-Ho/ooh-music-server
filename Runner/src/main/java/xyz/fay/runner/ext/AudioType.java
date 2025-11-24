package xyz.fay.runner.ext;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.util.Map;

public class AudioType extends MediaType {
    public static final AudioType FLAC;

    static {
        FLAC = new AudioType("audio", "flac");
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
