package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.User;

import java.util.List;

public interface UserService {
    void createUser(User user);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUserByEmail(String email);
    boolean validateUser(String username, String password);
    boolean isAccountLocked(String username);
    void unlockAccount(String username);
}