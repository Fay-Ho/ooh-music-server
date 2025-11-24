package xyz.fay.runner.model;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class MusicQuery {
    @Nullable private String type;
    @Nullable private String name;
}
