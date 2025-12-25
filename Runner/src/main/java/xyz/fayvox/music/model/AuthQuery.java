package xyz.fayvox.music.model;

import lombok.Data;

@Data
public final class AuthQuery {
    private String account;
    private Long pid;
}
