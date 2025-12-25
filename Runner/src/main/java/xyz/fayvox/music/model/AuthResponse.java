package xyz.fayvox.music.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AuthResponse {
    private String account;
    private Boolean isDestroy;
    private String name;
    private String phone;
    private String pid;
}
