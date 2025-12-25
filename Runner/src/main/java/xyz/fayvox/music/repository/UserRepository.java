package xyz.fayvox.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.fayvox.music.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByAccount(String account);
    User findUserByAccount(String account);
    Long deleteUserByAccount(String account);
}
