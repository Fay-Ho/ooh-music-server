package xyz.fayvox.music.model;

import lombok.Data;

@Data
public final class AuthBody {
    private String account;
    private String code;
    private String name;
    private String password;
    private String phone;
    private Long pid;
}
