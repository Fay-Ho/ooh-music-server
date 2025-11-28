package xyz.fay.runner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class MusicQueryResponse {
    private final String display;
    private final String ext;
    private final String name;
    private final String type;
}
