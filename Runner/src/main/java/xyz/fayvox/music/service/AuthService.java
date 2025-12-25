package xyz.fayvox.music.service;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import xyz.fayvox.music.common.Optional;
import xyz.fayvox.music.entity.User;
import xyz.fayvox.music.exception.InternalServerErrorException;
import xyz.fayvox.music.model.AuthBody;
import xyz.fayvox.music.model.AuthQuery;
import xyz.fayvox.music.model.AuthResponse;
import xyz.fayvox.music.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@AllArgsConstructor
@Service
public final class AuthService {
    private final Executor asyncTaskExecutor;
    private final UserRepository userRepository;

    @NonNull
    public CompletableFuture<AuthResponse> getAccount(@NonNull AuthQuery query) {
        return CompletableFuture.supplyAsync(
            () -> Optional
                .ofNullable(readUser(query))
                .map(this::setResponseBody)
                .orElseThrow(InternalServerErrorException::new),
            asyncTaskExecutor
        );
    }

    @NonNull
    public CompletableFuture<AuthResponse> postAccount(@NonNull AuthBody body) {
        return CompletableFuture.supplyAsync(
            () -> Optional
                .ofNullable(readUser(body))
                .map(this::setResponseBody)
                .orElseNullable(createUser(body))
                .map(this::setResponseBody)
                .orElseThrow(InternalServerErrorException::new),
            asyncTaskExecutor
        );
    }

    @NonNull
    public CompletableFuture<AuthResponse> deleteAccount(@NonNull String value) {
        return CompletableFuture.supplyAsync(
            () -> Optional
                .ofNullable(readUser(value))
                .map(u -> setResponseBody(u, deleteUser(value)))
                .orElseThrow(InternalServerErrorException::new),
            asyncTaskExecutor
        );
    }

    @Nullable
    private User createUser(@NonNull AuthBody body) {
        if (userRepository.existsUserByAccount(body.getAccount())) {
            return userRepository.findUserByAccount(body.getAccount());
        }

        return userRepository.save(User
            .builder()
            .account(body.getAccount())
            .name(body.getName())
            .password(body.getPassword())
            .phone(body.getPhone())
            .pid(System.currentTimeMillis())
            .build()
        );
    }

    @Nullable
    private <T> User readUser(@NonNull T t) {
        Map<Class<?>, Function<T, String>> extractor = new HashMap<>();
        extractor.put(String.class, v -> (String) v);
        extractor.put(AuthBody.class, v -> ((AuthBody) v).getAccount());
        extractor.put(AuthQuery.class, v -> ((AuthQuery) v).getAccount());

        return Optional
            .ofNullable(extractor.get(t.getClass()))
            .map(e -> e.apply(t))
            .map(userRepository::findUserByAccount)
            .get();
    }

    @NonNull
    private User updateUser(@NonNull User user) {
        return userRepository.save(user);
    }

    @NonNull
    private Boolean deleteUser(@NonNull String account) {
        return userRepository.deleteUserByAccount(account) > 0;
    }

    @NonNull
    private AuthResponse setResponseBody(@NonNull User user) {
        return AuthResponse
            .builder()
            .account(user.getAccount())
            .name(user.getName())
            .phone(user.getPhone())
            .pid(user.getPid().toString())
            .build();
    }

    @NonNull
    private AuthResponse setResponseBody(@NonNull User user, @NonNull Boolean isDestroy) {
        AuthResponse response = setResponseBody(user);
        response.setIsDestroy(isDestroy);
        return response;
    }
}
