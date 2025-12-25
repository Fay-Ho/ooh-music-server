package xyz.fayvox.music.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import xyz.fayvox.music.common.Optional;
import xyz.fayvox.music.common.response.ApiResponse;
import xyz.fayvox.music.exception.BadRequestException;
import xyz.fayvox.music.exception.NotFoundException;
import xyz.fayvox.music.model.AuthBody;
import xyz.fayvox.music.model.AuthQuery;
import xyz.fayvox.music.model.AuthResponse;
import xyz.fayvox.music.service.AuthService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public final class AuthController {
    private final AuthService authService;

    @GetMapping
    public CompletableFuture<ResponseEntity<Map<String, AuthResponse>>> request(@Nullable AuthQuery query) {
        return Optional.ofNullable(query).map(fn(authService::getAccount)).orElseThrow(BadRequestException::new);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Map<String, AuthResponse>>> request(@Nullable AuthBody body) {
        return Optional.ofNullable(body).map(fn(authService::postAccount)).orElseThrow(BadRequestException::new);
    }

    @DeleteMapping("/{account}")
    public CompletableFuture<ResponseEntity<Map<String, AuthResponse>>> request(@Nullable @PathVariable String account) {
        return Optional.ofNullable(account).map(fn(authService::deleteAccount)).orElseThrow(BadRequestException::new);
    }

    private <T, R> Function<T, CompletableFuture<ResponseEntity<Map<String, R>>>> fn(Function<T, CompletableFuture<R>> function) {
        return t -> function.apply(t).thenApply(this::isValidData).thenApply(ApiResponse::ok);
    }

    private <T> T isValidData(T data) {
        return Optional.ofNullable(data).orElseThrow(NotFoundException::new);
    }
}
