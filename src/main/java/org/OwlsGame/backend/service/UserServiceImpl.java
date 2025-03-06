package org.OwlsGame.backend.service;


import org.OwlsGame.backend.dao.UserDAO;
import org.OwlsGame.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    private ConcurrentHashMap<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> accountLockTime = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(1);

    @Override
    public void createUser(User user) {
        try {
            userDAO.addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUserByEmail(String email) {
        User user = null;
        try {
            user = userDAO.getUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = null;
        try {
            users = userDAO.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void updateUser(User user) {
        try {
            userDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByEmail(String email) {
        try {
            userDAO.deleteUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validateUser(String username, String password) {
        if (isAccountLocked(username)) {
            return false;
        }

        boolean isValid = false;
        try {
            isValid = userDAO.validateUser(username, password);
            if (isValid) {
                loginAttempts.remove(username);
                accountLockTime.remove(username);
            } else {
                incrementLoginAttempts(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public boolean isAccountLocked(String username) {
        if (!accountLockTime.containsKey(username)) {
            return false;
        }
        long lockTime = accountLockTime.get(username);
        if (System.currentTimeMillis() - lockTime > LOCK_TIME) {
            accountLockTime.remove(username);
            loginAttempts.remove(username);
            return false;
        }
        return true;
    }

    @Override
    public void unlockAccount(String username) {
        accountLockTime.remove(username);
        loginAttempts.remove(username);
    }

    private void incrementLoginAttempts(String username) {
        loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
        if (loginAttempts.get(username) >= MAX_ATTEMPTS) {
            accountLockTime.put(username, System.currentTimeMillis());
        }
    }
}