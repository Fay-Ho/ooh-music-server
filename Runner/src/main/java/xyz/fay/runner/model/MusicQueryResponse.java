package xyz.fay.runner.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MusicQueryResponse {
    private final String artist;
    private final String ext;
    private final String fullName;
    private final String name;
    private final String type;
}
