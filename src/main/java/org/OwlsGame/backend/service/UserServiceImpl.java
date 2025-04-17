package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.UserRepository;
import org.OwlsGame.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.OwlsGame.backend.dto.UserRegisterDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserService interface.
 * Handles user authentication, registration, lock/unlock logic, and CRUD operations.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_MINUTES = 5;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CRUD
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Query
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Auth
    /**
     * Validate user credentials and handle login attempts and lock logic.
     */
    @Override
    public boolean validateCredentials(String email, String password) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (!optUser.isPresent()) return false;
        User user = optUser.get();

        // Auto-unlock logic is handled in isAccountLocked
        if (isAccountLocked(user)) {
            return false;
        }

        // Plain text password comparison (for demo; use encryption in production)
        if (user.getPassword().equals(password)) {
            resetLoginAttempts(user);
            return true;
        } else {
            increaseLoginAttempts(user);
            if (user.getLoginAttempts() >= MAX_ATTEMPTS) {
                lockAccount(user, LOCK_MINUTES);
            }
            return false;
        }
    }

    // Login attempt management
    @Override
    public void increaseLoginAttempts(User user) {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
        userRepository.save(user);
    }

    @Override
    public void resetLoginAttempts(User user) {
        user.setLoginAttempts(0);
        user.setLockUntil(null);
        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public int getLoginAttempts(User user) {
        return user.getLoginAttempts();
    }

    // Lock and unlock logic

    @Override
    public void lockAccount(User user, int lockMinutes) {
        user.setLocked(true);
        user.setLockUntil(LocalDateTime.now().plusMinutes(lockMinutes));
        userRepository.save(user);
    }

    /**
     * Unlock the user account and reset attempts.
     */
    @Override
    public void unlockAccount(User user) {
        user.setLocked(false);
        user.setLockUntil(null);
        user.setLoginAttempts(0);
        userRepository.save(user);
    }


    @Override
    public boolean isAccountLocked(User user) {
        if (!user.isLocked()) {
            return false;
        }
        if (user.getLockUntil() == null) {
            return false;
        }

        if (user.getLockUntil().isBefore(LocalDateTime.now())) {
            unlockAccount(user);
            return false;
        }
        return true;
    }

    @Override
    public User registerUser(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        String plainPassword = userRegisterDto.getPassword();
        User user = new User(
                userRegisterDto.getFirstname(),
                userRegisterDto.getLastname(),
                plainPassword,
                userRegisterDto.getEmail()
        );
        user.setLoginAttempts(0);
        user.setLocked(false);
        user.setLockUntil(null);
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (validateCredentials(email, password)) {
                return user;
            }
        }
        return null;
    }
}