package xyz.fayvox.music.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MusicQueryResponse {
    private String artist;
    private String ext;
    private String fullName;
    private String name;
    private String type;

    public static MusicQueryResponse withArtist(String artist) {
        MusicQueryResponse response = new MusicQueryResponse();
        response.setArtist(artist);
        return response;
    }

    public static MusicQueryResponse withType(String type) {
        MusicQueryResponse response = new MusicQueryResponse();
        response.setType(type);
        return response;
    }
}
