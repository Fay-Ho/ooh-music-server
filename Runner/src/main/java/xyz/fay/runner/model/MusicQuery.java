package xyz.fay.runner.model;

import lombok.Data;

@Data
public final class MusicQuery {
    private String artist;
    private String category;
    private String name;
    private String search;
    private String type;
}
