package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.User;
import java.util.List;
import java.util.Optional;
import org.OwlsGame.backend.dto.UserRegisterDto;

public interface UserService {
    // 基础CRUD操作
    User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);

    // 扩展查询方法
    Optional<User> getUserByEmail(String email);

    // 安全验证方法
    boolean validateCredentials(String email, String password);
    void lockAccount(String email);
    void unlockAccount(String email);
    boolean isAccountLocked(String email);

    User registerUser(UserRegisterDto userRegisterDto);
}