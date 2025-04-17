package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.User;
import java.util.List;
import java.util.Optional;
import org.OwlsGame.backend.dto.UserRegisterDto;

public interface UserService {
    // CRUD
    User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);

    // Query
    Optional<User> getUserByEmail(String email);

    // Auth
    boolean validateCredentials(String email, String password);

    // Login attempts & lock
    void increaseLoginAttempts(User user);
    void resetLoginAttempts(User user);
    int getLoginAttempts(User user);

    void lockAccount(User user, int lockMinutes);
    void unlockAccount(User user);
    boolean isAccountLocked(User user);

    User registerUser(UserRegisterDto userRegisterDto);

    /**
     * 登录方法，返回User，如果失败返回null
     */
    User login(String email, String password);
}