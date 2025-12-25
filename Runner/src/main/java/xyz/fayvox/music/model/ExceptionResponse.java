package xyz.fayvox.music.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class ExceptionResponse {
    private final String[] allowedMethods;
}
